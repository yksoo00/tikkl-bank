const state = {
  memberId: null,
  home: null,
  transactions: [],
  cards: [],
  cardDetails: {},
  cardBenefits: {},
  financialProducts: [],
  cardProducts: [],
  myPage: null,
  benefitHistory: [],
  accounts: [],
  accountsLoaded: false,
  selectedAccountId: null,
  calendar: {
    date: new Date(),
    selected: null,
  },
  recentExpanded: false,
  transactionsExpanded: false,
};

let loading = false;

const statusText = document.getElementById("toolbarStatus");
const txCategoryFilter = document.getElementById("txCategoryFilter");
const txTypeFilter = document.getElementById("txTypeFilter");
const modalLayer = document.getElementById("modalLayer");
const ratioSlider = document.getElementById("savingsRatioSlider");
const recentMoreBtn = document.getElementById("recentMoreBtn");
const txListMoreBtn = document.getElementById("txListMoreBtn");

async function api(path, {method = "GET", body, headers} = {}) {
  const options = {
    method,
    headers: {
      Accept: "application/json",
      ...headers,
    },
  };
  if (body !== undefined) {
    options.headers["Content-Type"] = "application/json";
    options.body = JSON.stringify(body);
  }
  const res = await fetch(path, options);
  let json;
  try {
    json = await res.json();
  } catch (err) {
    throw new Error("응답 파싱 실패");
  }
  if (!res.ok || json.success === false) {
    throw new Error(json.message || `API 오류 (${res.status})`);
  }
  return json.data;
}

function setStatus(message, type) {
  if (statusText) {
    statusText.textContent = message;
    statusText.className = `status-text${type ? " " + type : ""}`;
  } else if (type === "error") {
    console.error(message);
  } else {
    console.info(message);
  }
}

function initializeMemberId() {
  const params = new URLSearchParams(window.location.search);
  const fromQuery = params.get("memberId");
  const savedSession = sessionStorage.getItem("tikklMemberId");
  const savedLocal = localStorage.getItem("tikklMemberId");
  const initial = fromQuery || savedSession || savedLocal;
  const parsed = Number(initial);
  if (!initial || Number.isNaN(parsed) || parsed <= 0) {
    setStatus("로그인 정보가 없어 기본 Member 1로 불러옵니다.", "error");
    state.memberId = 1;
    return;
  }
  state.memberId = parsed;
  if (!savedLocal && parsed) {
    localStorage.setItem("tikklMemberId", parsed);
  }
  if (!savedSession && parsed) {
    sessionStorage.setItem("tikklMemberId", parsed);
  }
}

async function loadAll({showStatus = true} = {}) {
  if (loading) return;
  if (!state.memberId) {
    initializeMemberId();
    if (!state.memberId) return;
  }
  loading = true;
  const memberId = state.memberId;
  if (showStatus) {
    setStatus("데이터 불러오는 중...", "loading");
  }

  try {
    const [home, myPage, transactionsPage, cards, financialProducts, cardProducts] =
      await Promise.all([
        api(`/members/${memberId}/home`),
        api(`/members/${memberId}/home/mypage`).catch(() => null),
        api(`/members/${memberId}/transactions/search`, {
          method: "POST",
          body: {page: 0, size: 100},
        }),
        api(`/members/${memberId}/cards`).catch(() => []),
        api(`/api/products`).catch(() => []),
        api(`/card-products`).catch(() => []),
      ]);

    state.home = home;
    state.myPage = myPage;
    state.transactions = transactionsPage?.content || [];
    state.cards = cards || [];
    state.financialProducts = financialProducts || [];
    state.cardProducts = cardProducts || [];
    state.cardDetails = {};
    state.cardBenefits = {};

    if (state.cards.length > 0) {
      const detailPromises = state.cards.map((card) =>
        api(`/members/${memberId}/cards/${card.id}`)
          .then((data) => ({cardId: card.id, data}))
          .catch(() => null)
      );

      const benefitsPromises = state.cards.map((card) =>
        api(`/card-products/${card.cardProductId}/benefits`)
          .then((data) => ({cardId: card.id, data}))
          .catch(() => ({cardId: card.id, data: []}))
      );

      const detailResults = await Promise.all(detailPromises);
      detailResults.forEach((entry) => {
        if (entry) {
          state.cardDetails[entry.cardId] = entry.data;
        }
      });

      const benefitResults = await Promise.all(benefitsPromises);
      benefitResults.forEach((entry) => {
        state.cardBenefits[entry.cardId] = entry.data || [];
      });
    }

    setupAccounts();
    buildBenefitHistory();
    state.recentExpanded = false;
    state.transactionsExpanded = false;
    renderAll();
    setStatus("데이터가 업데이트되었습니다.");
  } catch (err) {
    console.error(err);
    setStatus(err.message || "데이터 불러오기 실패", "error");
  } finally {
    loading = false;
  }
}

function setupAccounts() {
  const accounts = state.myPage?.accounts || [];
  state.accounts = accounts;
  state.accountsLoaded = false;
  const primary = accounts.find((acc) => acc.primary || acc.isPrimary);
  state.selectedAccountId = primary?.id || accounts[0]?.id || null;
  populateAccountSelect(accounts);
}

function populateAccountSelect(accounts = state.accounts) {
  const select = document.getElementById("cardAccountSelect");
  select.innerHTML = "";
  if (!accounts.length) {
    const option = document.createElement("option");
    option.textContent = "등록된 결제 계좌가 없습니다.";
    select.appendChild(option);
  } else {
    accounts.forEach((acc) => {
      const option = document.createElement("option");
      option.value = acc.id;
      option.textContent = `${acc.bankName} · ${maskAccount(acc.accountNumber)} (${formatCurrency(acc.balance)})`;
      if (state.selectedAccountId === acc.id) {
        option.selected = true;
      }
      select.appendChild(option);
    });
  }
}

function renderAll() {
  renderHome();
  renderTransactions();
  renderCards();
  renderMyPage();
  renderInvestmentPage();
}

function renderHome() {
  const savings = state.home?.savingsAccount;
  if (savings) {
    setText("savingsBalance", formatCurrency(savings.balance));
    const saved = formatCurrency(savings.totalSaved);
    const interest = formatAnnualRate(savings.interestRate);
    setText("savingsChange", `${saved} 적립 · ${interest}`);
    setText("savingsMeta", `${savings.productName || "티끌 통장"} · ${maskAccount(savings.accountNumber)}`);
  } else {
    setText("savingsBalance", "-");
    setText("savingsChange", "티끌 통장을 연결해보세요.");
    setText("savingsMeta", "티끌통장을 개설하면 자동으로 저축돼요.");
  }

  const billingEntries = state.cards.map((card) => {
    const detail = state.cardDetails[card.id];
    return detail?.nextBilling;
  }).filter(Boolean);

  const billingTotal = billingEntries.reduce((sum, entry) => sum + num(entry.totalAmount), 0);
  setText("nextBillingAmount", billingEntries.length ? formatCurrency(billingTotal) : "-");

  const sampleBilling = billingEntries[0];
  if (sampleBilling) {
    setText("nextBillingDesc", `${sampleBilling.month}월 ${sampleBilling.billingDay || 25}일 결제 예정`);
  } else {
    setText("nextBillingDesc", "청구 예정 데이터가 없습니다.");
  }

  const totalInterest = num(savings?.totalInterest);
  setText("interestTotal", totalInterest ? formatCurrency(totalInterest) : "-");
  const monthlyInterest = estimateMonthlyInterest(savings);
  setText(
    "interestDesc",
    monthlyInterest ? `이번 달 이자 ${formatCurrency(monthlyInterest)}` : "이번 달 이자 데이터가 없습니다."
  );

  renderProducts();
  renderCoachMessage();
  renderBenefitPreview();
  renderRecentTransactions();
}

function renderProducts() {
  const container = document.getElementById("productList");
  container.innerHTML = "";
  const products = state.financialProducts.slice(0, 3);
  if (!products.length) {
    container.innerHTML = `<p class="empty-state">추천 가능한 금융상품이 없습니다.</p>`;
    return;
  }
  products.forEach((product) => {
    const div = document.createElement("div");
    div.className = "product-card";
    div.innerHTML = `
      <div>
        <p><strong>${product.productName}</strong></p>
        <p>${product.provider || product.productType}</p>
        <p class="product-rate">${formatAnnualRate(product.interestRate || product.maxInterestRate)}</p>
      </div>
      <button class="link-btn" type="button" data-product-id="${product.id}">연결</button>
    `;
    container.appendChild(div);
  });

  container.querySelectorAll("[data-product-id]").forEach((button) => {
    button.addEventListener("click", () => {
      const productId = Number(button.dataset.productId);
      linkSavingsProduct(productId);
    });
  });
}

function renderInvestmentPage() {
  const hero = document.getElementById("investHero");
  const list = document.getElementById("investProductList");
  if (!hero || !list) return;

  const savings = state.home?.savingsAccount;
  hero.innerHTML = "";
  if (savings) {
    const rate = formatAnnualRate(savings.interestRate);
    const monthly = estimateMonthlyInterest(savings);
    hero.innerHTML = `
      <div class="investment-card">
        <div class="investment-meta">
          <div>
            <p class="label">현재 투자 중</p>
            <h3>${savings.productName || "티끌 통장"}</h3>
            <p>${savings.bankName || "저금 계좌"}</p>
          </div>
          <div class="rate">${rate}</div>
        </div>
        <div class="investment-stats">
          <div>
            <p class="label">투자 금액</p>
            <p>${formatCurrency(savings.balance)}</p>
          </div>
          <div>
            <p class="label">예상 월 수익</p>
            <p>${monthly ? `+${formatCurrency(monthly)}` : "0원"}</p>
          </div>
        </div>
        <div class="investment-tags">
          <span>원금 보장</span><span>예금자 보호</span><span>안전</span>
        </div>
      </div>
    `;
  } else {
    hero.innerHTML = `<div class="investment-card"><p>투자 중인 상품이 없습니다.</p><p>AI 추천 상품에서 선택해보세요.</p></div>`;
  }

  list.innerHTML = "";
  if (!state.financialProducts.length) {
    list.innerHTML = `<p class="empty-state">추천 가능한 금융상품이 없습니다.</p>`;
    return;
  }

  const balance = num(savings?.balance);
  state.financialProducts.forEach((product) => {
    const rateRaw = product.interestRate || product.maxInterestRate || 0;
    const rate = rateRaw > 1 ? rateRaw / 100 : rateRaw;
    const baseAmount = balance || num(product.minAmount) || 100000;
    const expectedMonthly = Math.round((baseAmount * rate) / 12);
    const tags = [
      product.productType,
      product.accountType || "원금 보장",
      "예금자 보호",
      "안전",
    ].filter(Boolean);
    const current = savings && product.productName === savings.productName;
    const statusTag = current ? '<span class="active">투자 중</span>' : "";
    const minLabel = product.minAmount ? formatCurrency(product.minAmount) : "-";
    const card = document.createElement("article");
    card.className = "invest-card";
    card.innerHTML = `
      <div class="invest-card-header">
        <div>
          <h3>${product.productName}</h3>
          <p>${product.provider || "-"}</p>
        </div>
        <div class="invest-card-rate">${formatAnnualRate(rateRaw)}</div>
      </div>
      <div class="invest-card-body">
        <div>
          <small>예상 월 수익</small>
          <span>+${formatCurrency(expectedMonthly)}</span>
        </div>
        <div>
          <small>최소 투자 금액</small>
          <span>${minLabel}</span>
        </div>
      </div>
      <div class="invest-card-tags">
        ${statusTag}
        ${tags.map((tag) => `<span>${tag}</span>`).join("")}
      </div>
      <button class="invest-action" data-product-link="${product.id}">
        이 상품으로 변경하기 →
      </button>
    `;
    list.appendChild(card);
  });

  list.querySelectorAll("[data-product-link]").forEach((button) => {
    button.addEventListener("click", () => {
      const productId = Number(button.dataset.productLink);
      if (Number.isNaN(productId)) return;
      linkSavingsProduct(productId);
    });
  });
}

function renderCoachMessage() {
  const coach = document.getElementById("coachMessage");
  const topBenefit = state.benefitHistory[0];
  if (topBenefit) {
    coach.textContent = `이번 주, ${topBenefit.card} 카드의 ${topBenefit.category} 혜택 ${topBenefit.rate}% 구간입니다.`;
  } else {
    coach.textContent = "혜택 데이터를 불러오는 중입니다.";
  }
}

function renderBenefitPreview() {
  const container = document.getElementById("benefitList");
  container.innerHTML = "";
  if (!state.benefitHistory.length) {
    container.innerHTML = `<p class="empty-state">이번 달 혜택 데이터가 없습니다.</p>`;
    return;
  }

  state.benefitHistory.slice(0, 3).forEach((item) => {
    const div = document.createElement("div");
    div.className = "benefit-item";
    div.innerHTML = `
      <div>
        <strong>${item.card}</strong>
        <p class="meta">${item.category} · ${formatDateShort(item.date)}</p>
      </div>
      <div>
        <p class="amount positive">+${formatCurrency(item.amount)}</p>
      </div>
    `;
    container.appendChild(div);
  });
}

function renderRecentTransactions() {
  const container = document.getElementById("recentTransactions");
  container.innerHTML = "";
  const transactions = state.home?.recentTransactions || [];
  if (!transactions.length) {
    container.innerHTML = `<p class="empty-state">최근 결제 내역이 없습니다.</p>`;
    if (recentMoreBtn) {
      recentMoreBtn.hidden = true;
    }
    return;
  }
  const limit = state.recentExpanded ? transactions.length : 5;
  transactions.slice(0, limit).forEach((tx) => {
    const div = document.createElement("div");
    div.className = `transaction-item ${tx.amount >= 0 ? "positive" : "negative"}`;
    const amount = tx.amount >= 0 ? `+${formatCurrency(tx.amount)}` : `-${formatCurrency(Math.abs(tx.amount))}`;
    div.innerHTML = `
      <div>
        <strong>${tx.merchant || tx.description}</strong>
        <p class="meta">${tx.category || tx.transactionType} · ${formatDateShort(tx.transactionAt)}</p>
      </div>
      <div class="amount">${amount}</div>
    `;
    container.appendChild(div);
  });
  if (recentMoreBtn) {
    recentMoreBtn.hidden = transactions.length <= 5;
    recentMoreBtn.textContent = state.recentExpanded ? "접기" : "더보기";
  }
}

function renderTransactions() {
  populateTransactionFilters();
  renderTransactionList();
  renderCalendar();
}

function populateTransactionFilters() {
  const categories = Array.from(
    new Set(state.transactions.map((tx) => tx.category).filter(Boolean))
  );
  txCategoryFilter.innerHTML = `<option value="">전체 카테고리</option>`;
  categories.forEach((category) => {
    const option = document.createElement("option");
    option.value = category;
    option.textContent = category;
    txCategoryFilter.appendChild(option);
  });
}

function renderTransactionList() {
  const type = txTypeFilter.value;
  const category = txCategoryFilter.value;
  const container = document.getElementById("transactionList");
  container.innerHTML = "";

  const filtered = state.transactions.filter((tx) => {
    if (type && tx.transactionType !== type) return false;
    if (category && tx.category !== category) return false;
    return true;
  });

  if (!filtered.length) {
    container.innerHTML = `<p class="empty-state">조건에 맞는 거래가 없습니다.</p>`;
    return;
  }

  const limit = state.transactionsExpanded ? filtered.length : 5;
  filtered.slice(0, limit).forEach((tx) => {
    const div = document.createElement("div");
    div.className = `transaction-item ${tx.amount >= 0 ? "positive" : "negative"}`;
    const amount = tx.amount >= 0 ? `+${formatCurrency(tx.amount)}` : `-${formatCurrency(Math.abs(tx.amount))}`;
    const savingsText = tx.savingsAmount ? ` · ${formatCurrency(tx.savingsAmount)} 저금` : "";
    div.innerHTML = `
      <div>
        <strong>${tx.merchant || tx.description}</strong>
        <p class="meta">${tx.transactionType} · ${formatDateLong(tx.transactionAt)}${savingsText}</p>
      </div>
      <div class="amount">${amount}</div>
    `;
    container.appendChild(div);
  });
  if (txListMoreBtn) {
    txListMoreBtn.hidden = filtered.length <= 5;
    txListMoreBtn.textContent = state.transactionsExpanded ? "접기" : "더보기";
  }
}

function renderCalendar() {
  const targetDate = state.calendar.date;
  const grid = document.getElementById("calendarGrid");
  const monthText = document.getElementById("calendarMonth");
  const detailTitle = document.getElementById("calendarSelectedDate");
  const detailList = document.getElementById("calendarTransactions");

  grid.innerHTML = "";
  const year = targetDate.getFullYear();
  const month = targetDate.getMonth();
  monthText.textContent = `${year}년 ${month + 1}월`;

  const firstDay = new Date(year, month, 1).getDay();
  const lastDate = new Date(year, month + 1, 0).getDate();

  for (let i = 0; i < firstDay; i++) {
    const blank = document.createElement("div");
    blank.className = "calendar-cell";
    blank.textContent = "";
    grid.appendChild(blank);
  }

  const map = groupTransactionsByDate();
  let selectedKey = state.calendar.selected;
  for (let day = 1; day <= lastDate; day++) {
    const key = `${year}-${pad(month + 1)}-${pad(day)}`;
    const cell = document.createElement("div");
    cell.className = "calendar-cell";
    cell.textContent = day;
    if (map[key]) {
      cell.classList.add("has-data");
    }
    if (!selectedKey && map[key]) {
      selectedKey = key;
    }
    if (selectedKey === key) {
      cell.classList.add("selected");
    }
    cell.addEventListener("click", () => {
      state.calendar.selected = key;
      renderCalendar();
    });
    grid.appendChild(cell);
  }

  const selectedTx = map[selectedKey] || [];
  state.calendar.selected = selectedKey;
  if (selectedKey) {
    detailTitle.textContent = formatDateReadable(selectedKey);
  } else {
    detailTitle.textContent = "선택된 날짜 없음";
  }

  detailList.innerHTML = "";
  if (!selectedTx.length) {
    detailList.innerHTML = `<p class="empty-state">해당 날짜 거래 내역이 없습니다.</p>`;
  } else {
    selectedTx.forEach((tx) => {
      const div = document.createElement("div");
      div.className = `transaction-item ${tx.amount >= 0 ? "positive" : "negative"}`;
      const amount =
        tx.amount >= 0 ? `+${formatCurrency(tx.amount)}` : `-${formatCurrency(Math.abs(tx.amount))}`;
      div.innerHTML = `
        <div>
          <strong>${tx.merchant || tx.description}</strong>
          <p class="meta">${tx.transactionType} · ${formatTime(tx.transactionAt)}</p>
        </div>
        <div class="amount">${amount}</div>
      `;
      detailList.appendChild(div);
    });
  }
}

function groupTransactionsByDate() {
  const map = {};
  state.transactions.forEach((tx) => {
    if (!tx.transactionAt) return;
    const date = tx.transactionAt.split("T")[0];
    if (!map[date]) {
      map[date] = [];
    }
    map[date].push(tx);
  });
  return map;
}

function renderCards() {
  const container = document.getElementById("cardsList");
  container.innerHTML = "";

  if (!state.cards.length) {
    container.innerHTML = `<p class="empty-state">등록된 카드가 없습니다.</p>`;
    return;
  }

  state.cards.forEach((card) => {
    const detail = state.cardDetails[card.id];
    const benefits = state.cardBenefits[card.id] || [];
    const progress = computeProgress(detail?.currentMonthSpending);
    const achieved = progress >= 100;
    const dueLabel = buildDueLabel(detail?.nextBilling);
    const dday = computeDDay(detail?.nextBilling);
    const benefitChips = benefits.slice(0, 3).map((b) => formatBenefitChip(b, achieved)).join("");
    const amount = detail?.nextBilling?.totalAmount;
    const div = document.createElement("div");
    div.className = `card-tile ${achieved ? "benefit-active" : "benefit-pending"}`;
    div.innerHTML = `
      <div class="card-head">
        <div>
          <p class="label">${card.cardNumber || "****"}</p>
          <h3>${card.cardName}</h3>
        </div>
        <div>
          <span class="chip">D-${dday}</span>
        </div>
      </div>
      <div class="benefit-status">${achieved ? "혜택 적용중" : "혜택 미달성"}</div>
      <div>
        <p class="label">다음 결제 예정</p>
        <p class="stat-value">${amount ? formatCurrency(amount) : "0원"}</p>
        <p class="stat-desc">${dueLabel}</p>
      </div>
      <div class="card-progress">
        <span style="width:${progress}%"></span>
      </div>
      <p class="stat-desc">혜택 구간 달성률 ${progress}%</p>
      <div class="chip-row">
        ${benefitChips || '<span class="chip">혜택 정보 없음</span>'}
      </div>
    `;
    container.appendChild(div);
  });
}

function renderMyPage() {
  const member = state.myPage?.member;
  const cards = state.cards.length;
  const totalBenefit = state.cards.reduce((sum, card) => sum + num(card.totalBenefitReceived), 0);

  if (member) {
    setText("profileAvatar", member.name ? member.name[0] : "티");
    setText("profileName", member.name || "-");
    setText("profileEmail", member.loginId || member.phoneNumber || "-");
  }
  setText("myCardCount", `${cards}`);
  setText("myBenefitEarned", formatCurrency(totalBenefit));
  setText("myUsageDays", `${estimateUsageDays(member)}일`);
  document.getElementById("ratioValue").textContent = `${ratioSlider.value}%`;
  document.getElementById("savingsRatioLabel").textContent = `${ratioSlider.value}%`;
}

function renderSavingsModal() {
  const historyContainer = document.getElementById("savingsHistory");
  const accountsContainer = document.getElementById("savingsAccounts");
  historyContainer.innerHTML = "";
  accountsContainer.innerHTML = "";
  const summary = state.home?.savingsAccount;
  if (summary) {
    setText("savingsModalBalance", formatCurrency(summary.balance));
    setText("savingsModalDesc", `${summary.productName || "티끌 통장"} · ${maskAccount(summary.accountNumber)}`);
    setText("savingsModalProduct", summary.productName || "티끌 통장");
  } else {
    setText("savingsModalBalance", "0원");
    setText("savingsModalDesc", "티끌 통장을 연결하면 자동으로 저금돼요.");
    setText("savingsModalProduct", "티끌 통장");
  }

  const savingsHistory = state.transactions.filter(
    (tx) => tx.transactionType === "SAVINGS" || tx.transactionType === "INTEREST"
  );

  if (!savingsHistory.length) {
    historyContainer.innerHTML = `<p class="empty-state">저금 이력이 아직 없습니다.</p>`;
  } else {
    savingsHistory.slice(0, 6).forEach((tx) => {
      const div = document.createElement("div");
      div.className = "transaction-item positive";
      div.innerHTML = `
        <div>
          <strong>${tx.description || tx.transactionType}</strong>
          <p class="meta">${formatDateLong(tx.transactionAt)}</p>
        </div>
        <div class="amount">+${formatCurrency(tx.amount)}</div>
      `;
      historyContainer.appendChild(div);
    });
  }

  state.accounts.forEach((acc) => {
    const wrap = document.createElement("label");
    wrap.className = "benefit-item";
    wrap.innerHTML = `
      <input type="radio" name="primaryAccount" value="${acc.id}" ${state.selectedAccountId === acc.id ? "checked" : ""}/>
      <div>
        <strong>${acc.bankName}</strong>
        <p class="meta">${maskAccount(acc.accountNumber)} · ${formatCurrency(acc.balance)}</p>
      </div>
      <div class="amount">${acc.primary || acc.isPrimary ? "대표" : ""}</div>
    `;
    wrap.querySelector("input").addEventListener("change", () => {
      state.selectedAccountId = acc.id;
    });
    accountsContainer.appendChild(wrap);
  });

  if (!state.accounts.length) {
    const emptyMessage = state.accountsLoaded
      ? "연결된 계좌가 없습니다."
      : "계좌 정보를 불러오는 중입니다...";
    accountsContainer.innerHTML = `<p class="empty-state">${emptyMessage}</p>`;
  }
}

async function fetchAccounts() {
  if (!state.memberId) return;
  try {
    const accounts = await api(`/members/${state.memberId}/accounts`);
    state.accounts = accounts || [];
    state.accountsLoaded = true;
    populateAccountSelect();
    renderSavingsModal();
  } catch (err) {
    console.error(err);
    setStatus(err.message || "계좌 정보를 불러오지 못했습니다.", "error");
  }
}

async function linkSavingsProduct(productId) {
  if (!state.memberId || !productId) return;
  try {
    await api(`/members/${state.memberId}/savings/product`, {
      method: "PUT",
      body: {productId},
    });
    setStatus("선택한 상품으로 저금 상품이 연결되었습니다.");
    await loadAll({showStatus: false});
  } catch (err) {
    console.error(err);
    setStatus(err.message || "상품 연결에 실패했습니다.", "error");
  }
}

function renderBillingModal() {
  const container = document.getElementById("billingList");
  container.innerHTML = "";
  const items = state.cards.map((card) => {
    const billing = state.cardDetails[card.id]?.nextBilling;
    return {
      card: card.cardName,
      amount: billing?.totalAmount || 0,
      due: billing ? `${billing.month}월 ${billing.billingDay || 25}일` : "결제일 미정",
    };
  }).filter((item) => item.amount > 0);

  if (!items.length) {
    container.innerHTML = `<p class="empty-state">청구 예정 금액이 없습니다.</p>`;
  } else {
    items.forEach((item) => {
      const div = document.createElement("div");
      div.className = "billing-row";
      div.innerHTML = `
        <div>
          <strong>${item.card}</strong>
          <p class="meta">${item.due}</p>
        </div>
        <div class="billing-amount">${formatCurrency(item.amount)}</div>
      `;
      container.appendChild(div);
    });
  }

  const total = items.reduce((sum, item) => sum + item.amount, 0);
  setText("billingTotal", formatCurrency(total));
}

function renderBenefitModal() {
  const container = document.getElementById("benefitModalList");
  container.innerHTML = "";
  if (!state.benefitHistory.length) {
    container.innerHTML = `<p class="empty-state">이번 달 혜택 내역이 없습니다.</p>`;
    return;
  }
  state.benefitHistory.forEach((item) => {
    const div = document.createElement("div");
    div.className = "benefit-item";
    div.innerHTML = `
      <div>
        <strong>${item.card}</strong>
        <p class="meta">${item.category} · ${formatDateLong(item.date)}</p>
      </div>
      <div class="amount positive">+${formatCurrency(item.amount)}</div>
    `;
    container.appendChild(div);
  });
}

function buildBenefitHistory() {
  const benefits = [];
  state.cards.forEach((card) => {
    const productBenefits = state.cardBenefits[card.id] || [];
    productBenefits.slice(0, 3).forEach((benefit, index) => {
      const amountBase = benefit.maxDiscountPerMonth
        ? num(benefit.maxDiscountPerMonth)
        : 5000 + index * 1000;
      benefits.push({
        card: card.cardName,
        category: formatCategoryName(benefit.categoryCode || benefit.benefitName),
        rate: Math.round((benefit.discountRate || 0.1) * 100),
        amount: Math.round(amountBase * (0.4 + Math.random() * 0.5)),
        date: randomRecentDate(index),
      });
    });
  });
  state.benefitHistory = benefits.sort((a, b) => b.amount - a.amount).slice(0, 8);
}

function maskAccount(value) {
  if (!value) return "-";
  return value.replace(/(\d{2,3})\d+(\d{2})/, "$1***$2");
}

function formatCurrency(value) {
  const amount = num(value);
  if (!amount) return "0원";
  return `${amount.toLocaleString("ko-KR")}원`;
}

function formatAnnualRate(value) {
  const rate = Number(value || 0);
  if (!rate) return "연 0%";
  const percent = rate > 1 ? rate : rate * 100;
  const formatted = Number.isInteger(percent) ? percent : Number(percent.toFixed(1));
  return `연 ${formatted}%`;
}

function formatPercent(value) {
  const n = Number(value || 0);
  return `${(n * 100 || n).toFixed(1)}%`;
}

function num(value) {
  const converted = Number(value);
  return Number.isNaN(converted) ? 0 : converted;
}

function pad(num) {
  return num.toString().padStart(2, "0");
}

function formatDateShort(value) {
  if (!value) return "-";
  const date = new Date(value);
  if (Number.isNaN(date.getTime()) && typeof value === "string") {
    return value.replace("T", " ").slice(5, 10);
  }
  return `${pad(date.getMonth() + 1)}.${pad(date.getDate())}`;
}

function formatDateLong(value) {
  if (!value) return "-";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`;
}

function formatDateReadable(value) {
  if (!value) return "-";
  const [y, m, d] = value.split("-");
  return `${y}년 ${Number(m)}월 ${Number(d)}일`;
}

function formatTime(value) {
  if (!value) return "-";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value.split("T")[1]?.slice(0, 5) || value;
  return `${pad(date.getHours())}:${pad(date.getMinutes())}`;
}

function estimateMonthlyInterest(savings) {
  if (!savings) return 0;
  const balance = num(savings.balance);
  const rawRate = Number(savings.interestRate || 0);
  const rate = rawRate > 1 ? rawRate / 100 : rawRate;
  if (!balance || !rate) return 0;
  return Math.round((balance * rate) / 12);
}

function computeProgress(amount) {
  const target = 500000;
  const spending = num(amount);
  const progress = Math.min(100, Math.round((spending / target) * 100));
  return progress;
}

function buildDueLabel(billing) {
  if (!billing) return "결제 예정 없음";
  return `${billing.year}.${pad(billing.month)} · ${billing.transactionCount || 0}건`;
}

function computeDDay(billing) {
  if (!billing) return 30;
  const dueDate = new Date(billing.year, billing.month - 1, billing.billingDay || 25);
  const diff = Math.round((dueDate - new Date()) / (1000 * 60 * 60 * 24));
  return diff > 0 ? diff : 0;
}

function formatBenefitChip(benefit, achieved) {
  const name = benefit.benefitName || formatCategoryName(benefit.categoryCode);
  const rate = benefit.discountRate ? `${Math.round(benefit.discountRate * 100)}%` : "";
  const text = [name, rate].filter(Boolean).join(" ");
  return `<span class="chip ${achieved ? "active" : "inactive"}">${text}</span>`;
}

function formatCategoryName(code) {
  if (!code) return "기타";
  const map = {
    CONVENIENCE: "편의점",
    CONVENIENCE_STORE: "편의점",
    CAFE: "카페",
    MART: "마트",
    DELIVERY: "배달",
    TRANSPORT: "대중교통",
    BEAUTY: "뷰티",
    FUEL: "주유",
  };
  return map[code] || code;
}

function randomRecentDate(offset = 0) {
  const base = new Date();
  base.setDate(base.getDate() - (offset * 3 + Math.floor(Math.random() * 5)));
  return base.toISOString();
}

function shuffle(list) {
  const result = [...list];
  for (let i = result.length - 1; i > 0; i -= 1) {
    const j = Math.floor(Math.random() * (i + 1));
    [result[i], result[j]] = [result[j], result[i]];
  }
  return result;
}

function estimateUsageDays(member) {
  if (!member) return 0;
  const start = (member?.createdAt && new Date(member.createdAt)) || new Date(Date.now() - 60 * 24 * 3600 * 1000);
  return Math.max(1, Math.round((Date.now() - start.getTime()) / (1000 * 60 * 60 * 24)));
}

function setText(id, value) {
  const el = document.getElementById(id);
  if (el) {
    el.textContent = value;
  }
}

function setScreen(screenId) {
  document.querySelectorAll(".screen").forEach((screen) => {
    screen.classList.toggle("active", screen.id === `screen-${screenId}`);
  });
  document.querySelectorAll(".bottom-nav button").forEach((btn) => {
    btn.classList.toggle("active", btn.dataset.screen === screenId);
  });
}

function openModal(id) {
  modalLayer.hidden = false;
  modalLayer.querySelectorAll(".modal").forEach((modal) => {
    modal.classList.toggle("active", modal.id === id);
  });
  if (id === "savingsModal") {
    renderSavingsModal();
    fetchAccounts();
  } else if (id === "billingModal") {
    renderBillingModal();
  } else if (id === "benefitModal") {
    renderBenefitModal();
  } else if (id === "addCardModal") {
    populateCardProductSelect();
  }
}

function closeModal() {
  modalLayer.hidden = true;
  modalLayer.querySelectorAll(".modal").forEach((modal) => modal.classList.remove("active"));
}

function populateCardProductSelect() {
  const select = document.getElementById("cardProductSelect");
  select.innerHTML = "";
  if (!state.cardProducts.length) {
    select.innerHTML = `<option>등록 가능한 상품이 없습니다.</option>`;
    return;
  }
  state.cardProducts.forEach((product) => {
    const option = document.createElement("option");
    option.value = product.id;
    option.textContent = `${product.company} · ${product.name}`;
    select.appendChild(option);
  });
}

async function loadTransactions() {
  if (!state.memberId) return;
  try {
    const data = await api(`/members/${state.memberId}/transactions/search`, {
      method: "POST",
      body: {page: 0, size: 100},
    });
    state.transactions = data?.content || [];
    renderTransactions();
    setStatus("거래 내역이 새로고침되었습니다.");
  } catch (err) {
    console.error(err);
    setStatus(err.message || "거래 내역 불러오기 실패", "error");
  }
}

async function updatePrimaryAccount() {
  if (!state.memberId || !state.selectedAccountId) {
    alert("대표 계좌로 지정할 계좌를 선택해주세요.");
    return;
  }
  try {
    await api(`/members/${state.memberId}/accounts/${state.selectedAccountId}/primary`, {
      method: "PUT",
    });
    setStatus("대표 계좌가 변경되었습니다.");
    closeModal();
    await loadAll({showStatus: false});
  } catch (err) {
    console.error(err);
    setStatus(err.message || "대표 계좌 변경 실패", "error");
  }
}

async function handleAddCard(event) {
  event.preventDefault();
  if (!state.memberId) {
    alert("Member ID가 필요합니다.");
    return;
  }
  const form = event.target;
  const formData = new FormData(form);
  const productId = formData.get("productId");
  const accountId = formData.get("accountId");
  const nickname = formData.get("nickname") || "";
  if (!productId || !accountId) {
    alert("카드 상품과 결제 계좌를 선택해주세요.");
    return;
  }
  const url = `/members/${state.memberId}/cards?productId=${productId}&accountId=${accountId}&nickname=${encodeURIComponent(nickname)}`;
  try {
    await api(url, {method: "POST"});
    setStatus("새 카드가 추가되었습니다.");
    closeModal();
    form.reset();
    await loadAll({showStatus: false});
  } catch (err) {
    console.error(err);
    setStatus(err.message || "카드 추가 실패", "error");
  }
}

document.querySelectorAll("[data-modal]").forEach((trigger) => {
  trigger.addEventListener("click", () => openModal(trigger.dataset.modal));
});
document.getElementById("changePrimaryAccount").addEventListener("click", updatePrimaryAccount);
document.getElementById("txRefresh").addEventListener("click", loadTransactions);
document.getElementById("calendarPrev").addEventListener("click", () => {
  state.calendar.date.setMonth(state.calendar.date.getMonth() - 1);
  renderCalendar();
});
document.getElementById("calendarNext").addEventListener("click", () => {
  state.calendar.date.setMonth(state.calendar.date.getMonth() + 1);
  renderCalendar();
});

ratioSlider.addEventListener("input", (event) => {
  const value = `${event.target.value}%`;
  setText("ratioValue", value);
  setText("savingsRatioLabel", value);
});

[txCategoryFilter, txTypeFilter].forEach((select) =>
  select.addEventListener("change", renderTransactionList)
);

document.querySelectorAll(".segmented button").forEach((button) => {
  button.addEventListener("click", () => {
    document.querySelectorAll(".segmented button").forEach((btn) => btn.classList.remove("active"));
    button.classList.add("active");
    const targetView = button.dataset.view;
    document.querySelectorAll(".transactions-view").forEach((view) => {
      view.classList.toggle("hidden", view.dataset.view !== targetView);
    });
  });
});

document.querySelectorAll(".bottom-nav button").forEach((button) => {
  button.addEventListener("click", () => setScreen(button.dataset.screen));
});

document.querySelectorAll("[data-screen-target]").forEach((button) => {
  button.addEventListener("click", () => setScreen(button.dataset.screenTarget));
});

modalLayer.addEventListener("click", (event) => {
  if (event.target === modalLayer || event.target.hasAttribute("data-close")) {
    closeModal();
  }
});

if (recentMoreBtn) {
  recentMoreBtn.addEventListener("click", () => {
    state.recentExpanded = !state.recentExpanded;
    renderRecentTransactions();
  });
}

if (txListMoreBtn) {
  txListMoreBtn.addEventListener("click", () => {
    state.transactionsExpanded = !state.transactionsExpanded;
    renderTransactionList();
  });
}

document.querySelectorAll(".tabs button").forEach((button) => {
  button.addEventListener("click", () => {
    const tabs = button.closest(".tabs");
    const modal = button.closest(".modal");
    const target = button.dataset.tab;
    tabs.querySelectorAll("button").forEach((btn) => btn.classList.toggle("active", btn === button));
    modal.querySelectorAll(".tab-panel").forEach((panel) => {
      panel.classList.toggle("active", panel.dataset.tabPanel === target);
    });
    if (target === "accounts") {
      fetchAccounts();
    }
  });
});

document.getElementById("addCardForm").addEventListener("submit", handleAddCard);

window.addEventListener("DOMContentLoaded", () => {
  initializeMemberId();
  loadAll();
});
