const state = {
  memberId: null,
  accounts: [],
  cards: [],
  cardProducts: [],
};

const memberBadge = document.getElementById("currentMember");
const depositAccountSelect = document.getElementById("depositAccount");
const withdrawAccountSelect = document.getElementById("withdrawAccount");
const cardSelect = document.getElementById("paymentCard");
const benefitProductSelect = document.getElementById("benefitCardProduct");
const logArea = document.getElementById("logArea");

const depositForm = document.getElementById("form-deposit");
const withdrawForm = document.getElementById("form-withdraw");
const cardForm = document.getElementById("form-card-payment");
const cardProductForm = document.getElementById("form-cardproduct");
const cardBenefitForm = document.getElementById("form-cardbenefit");

function log(message, data) {
  const time = new Date().toLocaleTimeString();
  const line = `[${time}] ${message}${data ? `\n${JSON.stringify(data, null, 2)}` : ""}\n`;
  logArea.textContent = `${line}${logArea.textContent}`;
}

function handleError(err) {
  console.error(err);
  log(`❌ ${err.message || "요청 실패"}`);
}

async function api(path, options = {}) {
  const res = await fetch(path, {
    method: options.method || "GET",
    headers: {
      Accept: "application/json",
      "Content-Type": "application/json",
    },
    body: options.body ? JSON.stringify(options.body) : undefined,
  });
  const json = await res.json().catch(() => ({}));
  if (!res.ok || json.success === false) {
    throw new Error(json.message || `API 오류 (${res.status})`);
  }
  return json.data;
}

function resolveMemberId() {
  const params = new URLSearchParams(window.location.search);
  const queryId = params.get("memberId");
  const sessionId = sessionStorage.getItem("tikklMemberId");
  const localId = localStorage.getItem("tikklMemberId");
  const parsed = Number(queryId || sessionId || localId);
  if (!parsed) {
    window.location.href = "auth.html";
    return;
  }
  state.memberId = parsed;
  sessionStorage.setItem("tikklMemberId", parsed);
  localStorage.setItem("tikklMemberId", parsed);
  memberBadge.textContent = `Member #${parsed}`;
}

async function loadData() {
  if (!state.memberId) resolveMemberId();
  if (!state.memberId) return;
  try {
    const [accounts, cards, cardProducts] = await Promise.all([
      api(`/members/${state.memberId}/accounts`),
      api(`/members/${state.memberId}/cards`).catch(() => []),
      api(`/card-products`).catch(() => []),
    ]);
    state.accounts = accounts || [];
    state.cards = cards || [];
    state.cardProducts = cardProducts || [];
    populateSelects();
    log("✅ 계좌/카드 정보를 새로고침했습니다.");
  } catch (err) {
    handleError(err);
  }
}

function populateSelects() {
  [depositAccountSelect, withdrawAccountSelect].forEach((select) => {
    select.innerHTML = "";
    if (!state.accounts.length) {
      select.innerHTML = `<option disabled selected>등록된 계좌 없음</option>`;
      select.disabled = true;
      return;
    }
    select.disabled = false;
    state.accounts.forEach((acc, index) => {
      const option = document.createElement("option");
      option.value = acc.id;
      option.textContent = `${acc.bankName} · ${maskAccount(acc.accountNumber)} (잔액 ${formatCurrency(acc.balance)})`;
      if (index === 0) option.selected = true;
      select.appendChild(option);
    });
  });

  cardSelect.innerHTML = "";
  if (!state.cards.length) {
    cardSelect.innerHTML = `<option disabled selected>등록된 카드 없음</option>`;
    cardSelect.disabled = true;
  } else {
    cardSelect.disabled = false;
    state.cards.forEach((card, index) => {
      const option = document.createElement("option");
      option.value = card.id;
      option.textContent = `${card.cardName} · ${card.cardNumber || "****"}`;
      if (index === 0) option.selected = true;
      cardSelect.appendChild(option);
    });
  }

  benefitProductSelect.innerHTML = "";
  if (!state.cardProducts.length) {
    benefitProductSelect.innerHTML = `<option disabled selected>등록된 마스터카드 없음</option>`;
    benefitProductSelect.disabled = true;
  } else {
    benefitProductSelect.disabled = false;
    state.cardProducts.forEach((product, index) => {
      const option = document.createElement("option");
      option.value = product.id;
      option.textContent = `${product.company} · ${product.name}`;
      if (index === 0) option.selected = true;
      benefitProductSelect.appendChild(option);
    });
  }
}

function maskAccount(value) {
  if (!value) return "-";
  return value.replace(/(\d{2,3})\d+(\d{2})/, "$1***$2");
}

function formatCurrency(value) {
  const num = Number(value) || 0;
  return `${num.toLocaleString("ko-KR")}원`;
}

depositForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  const accountId = Number(depositAccountSelect.value);
  const amount = Number(document.getElementById("depositAmount").value);
  const description = document.getElementById("depositDesc").value;
  if (!accountId || !amount) return;
  try {
    const data = await api(`/members/${state.memberId}/accounts/${accountId}/deposit`, {
      method: "POST",
      body: {amount, description},
    });
    log("💰 입금 성공", data);
    await loadData();
    depositForm.reset();
  } catch (err) {
    handleError(err);
  }
});

withdrawForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  const accountId = Number(withdrawAccountSelect.value);
  const amount = Number(document.getElementById("withdrawAmount").value);
  const description = document.getElementById("withdrawDesc").value;
  if (!accountId || !amount) return;
  try {
    const data = await api(`/members/${state.memberId}/accounts/${accountId}/withdraw`, {
      method: "POST",
      body: {amount, description},
    });
    log("🏧 출금 성공", data);
    await loadData();
    withdrawForm.reset();
  } catch (err) {
    handleError(err);
  }
});

cardForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  const cardId = Number(cardSelect.value);
  const amount = Number(document.getElementById("paymentAmount").value);
  const merchant = document.getElementById("paymentMerchant").value;
  const category = document.getElementById("paymentCategory").value;
  const description = document.getElementById("paymentDesc").value;
  if (!cardId || !amount || !merchant) return;
  try {
    const body = {amount, merchant, category, description};
    const data = await api(`/members/${state.memberId}/cards/${cardId}/payments`, {
      method: "POST",
      body,
    });
    log("🧾 카드 결제 생성", data);
    cardForm.reset();
  } catch (err) {
    handleError(err);
  }
});

document.getElementById("simulateDay").addEventListener("click", async () => {
  try {
    const data = await api("/api/dev/simulate/day", {method: "POST"});
    log("⏱️ 하루 이자 시뮬레이션 완료", data);
  } catch (err) {
    handleError(err);
  }
});

document.getElementById("simulateMonth").addEventListener("click", async () => {
  try {
    const data = await api("/api/dev/simulate/month-end", {method: "POST"});
    log("📅 월말 결제 시뮬레이션 완료", data);
  } catch (err) {
    handleError(err);
  }
});

document.getElementById("reloadData").addEventListener("click", loadData);
document.getElementById("clearLog").addEventListener("click", () => {
  logArea.textContent = "// 로그가 초기화되었습니다.";
});

cardProductForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  const payload = {
    name: document.getElementById("cpName").value,
    company: document.getElementById("cpCompany").value,
    cardType: document.getElementById("cpType").value,
    annualFee: toNumber(document.getElementById("cpAnnualFee").value),
    description: document.getElementById("cpDescription").value,
    imageUrl: document.getElementById("cpImageUrl").value,
    summaryBenefits: document.getElementById("cpSummary").value,
  };
  try {
    const data = await api("/card-products", {
      method: "POST",
      body: payload,
    });
    log("🆕 마스터카드가 생성되었습니다.", data);
    cardProductForm.reset();
    await loadData();
  } catch (err) {
    handleError(err);
  }
});

cardBenefitForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  const productId = Number(benefitProductSelect.value);
  if (!productId) return;
  const payload = {
    benefitName: document.getElementById("benefitName").value,
    benefitType: document.getElementById("benefitType").value,
    categoryCode: document.getElementById("benefitCategory").value,
    discountRate: toNumber(document.getElementById("benefitRate").value),
    maxDiscountPerMonth: toNumber(document.getElementById("benefitMax").value),
    minSpendingForActivation: toNumber(document.getElementById("benefitMinSpend").value),
    description: document.getElementById("benefitDescription").value,
  };
  try {
    const data = await api(`/card-products/${productId}/benefits`, {
      method: "POST",
      body: payload,
    });
    log("🎁 혜택이 추가되었습니다.", data);
    cardBenefitForm.reset();
    await loadData();
  } catch (err) {
    handleError(err);
  }
});

function toNumber(value) {
  const num = Number(value);
  return Number.isNaN(num) ? 0 : num;
}

window.addEventListener("DOMContentLoaded", () => {
  resolveMemberId();
  loadData();
});
