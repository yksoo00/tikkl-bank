// ----- 공통 상태 -----
let currentMemberId = null;
let currentAccountId = null;
let currentCardId = null;

const elMember = document.getElementById("currentMemberId");
const elAccount = document.getElementById("currentAccountId");
const elCard = document.getElementById("currentCardId");
const logOutput = document.getElementById("log-output");

// ----- 유틸 -----
function setCurrentMember(id) {
  if (!id) {
    return;
  }
  currentMemberId = id;
  elMember.textContent = String(id);
}

function setCurrentAccount(id) {
  if (!id) {
    return;
  }
  currentAccountId = id;
  elAccount.textContent = String(id);
}

function setCurrentCard(id) {
  if (!id) {
    return;
  }
  currentCardId = id;
  elCard.textContent = String(id);
}

function log(title, data) {
  const time = new Date().toISOString().slice(11, 19);
  const payload =
      typeof data === "string" ? data : JSON.stringify(data, null, 2);
  logOutput.textContent =
      `[${time}] ${title}\n${payload}\n\n` + logOutput.textContent;
}

// 공통 fetch
async function apiCall(method, url, body) {
  const options = {
    method,
    headers: {
      "Content-Type": "application/json",
    },
  };
  if (body !== undefined && body !== null) {
    options.body = JSON.stringify(body);
  }

  const res = await fetch(url, options);
  let json;
  try {
    json = await res.json();
  } catch (e) {
    throw new Error(`응답 JSON 파싱 실패 (${res.status})`);
  }
  if (!res.ok || json.success === false) {
    const msg = json.message || `HTTP ${res.status}`;
    const err = new Error(msg);
    err.response = json;
    throw err;
  }
  return json;
}

// 폼에서 값 읽기
function formToObject(form) {
  const obj = {};
  new FormData(form).forEach((value, key) => {
    if (value === "") {
      return;
    }
    if (value === "true") {
      obj[key] = true;
    } else if (value === "false") {
      obj[key] = false;
    } else if (!isNaN(value) && value.trim() !== "" && key !== "phoneNumber") {
      // 숫자스러우면 숫자로 (단, phoneNumber는 문자열 유지)
      obj[key] = Number(value);
    } else {
      obj[key] = value;
    }
  });
  return obj;
}

function resolveMemberId(fieldValue) {
  return fieldValue && fieldValue !== ""
      ? fieldValue
      : currentMemberId;
}

// ----- 네비게이션 -----
document.querySelectorAll(".nav-item").forEach((btn) => {
  btn.addEventListener("click", () => {
    document
    .querySelectorAll(".nav-item")
    .forEach((b) => b.classList.remove("active"));
    btn.classList.add("active");

    const sectionId = btn.dataset.section;
    document
    .querySelectorAll(".section")
    .forEach((sec) => sec.classList.remove("visible"));
    document.getElementById(sectionId).classList.add("visible");
  });
});

document.getElementById("btn-clear-log").addEventListener("click", () => {
  logOutput.textContent = "";
});

// ======================
// 1. 회원가입 / 로그인
// ======================
document
.getElementById("form-signup")
.addEventListener("submit", async (e) => {
  e.preventDefault();
  const body = formToObject(e.target);
  try {
    const json = await apiCall("POST", "/members/signup", body);
    log("POST /members/signup", json);
    if (json.data && json.data.id) {
      setCurrentMember(json.data.id);
    }
  } catch (err) {
    log("POST /members/signup ERROR", err.response || err.message);
  }
});

document.getElementById("form-login").addEventListener("submit", async (e) => {
  e.preventDefault();
  const body = formToObject(e.target);
  try {
    const json = await apiCall("POST", "/members/login", body);
    log("POST /members/login", json);
    if (json.data && json.data.id) {
      setCurrentMember(json.data.id);
    }
  } catch (err) {
    log("POST /members/login ERROR", err.response || err.message);
  }
});

// ======================
// 2. 회원 설정
// ======================
document
.getElementById("form-get-member")
.addEventListener("submit", async (e) => {
  e.preventDefault();
  const memberId = resolveMemberId(e.target.memberId.value);
  if (!memberId) {
    return alert("Member ID가 없습니다.");
  }

  try {
    const json = await apiCall("GET", `/members/${memberId}`);
    log(`GET /members/${memberId}`, json);
    if (json.data && json.data.id) {
      setCurrentMember(json.data.id);
    }
  } catch (err) {
    log("GET /members/{id} ERROR", err.response || err.message);
  }
});

document
.getElementById("form-savings-settings")
.addEventListener("submit", async (e) => {
  e.preventDefault();
  const raw = formToObject(e.target);
  const memberId = resolveMemberId(raw.memberId);
  if (!memberId) {
    return alert("Member ID가 없습니다.");
  }

  const body = {
    savingsRatio:
        raw.savingsRatio !== undefined ? raw.savingsRatio : null,
    autoSavingsEnabled:
        raw.autoSavingsEnabled !== undefined
            ? raw.autoSavingsEnabled
            : null,
  };

  try {
    const json = await apiCall(
        "PUT",
        `/members/${memberId}/settings/savings`,
        body
    );
    log(`PUT /members/${memberId}/settings/savings`, json);
  } catch (err) {
    log("PUT /members/{id}/settings/savings ERROR",
        err.response || err.message);
  }
});

document
.getElementById("form-onboarding")
.addEventListener("submit", async (e) => {
  e.preventDefault();
  const memberId = resolveMemberId(e.target.memberId.value);
  if (!memberId) {
    return alert("Member ID가 없습니다.");
  }
  try {
    const json = await apiCall("PUT", `/members/${memberId}/onboarding`);
    log(`PUT /members/${memberId}/onboarding`, json);
  } catch (err) {
    log("PUT /members/{id}/onboarding ERROR", err.response || err.message);
  }
});

// ======================
// 3. 홈 / 마이페이지
// ======================
document.getElementById("form-home").addEventListener("submit", async (e) => {
  e.preventDefault();
  const memberId = resolveMemberId(e.target.memberId.value);
  if (!memberId) {
    return alert("Member ID가 없습니다.");
  }
  try {
    const json = await apiCall("GET", `/members/${memberId}/home`);
    log(`GET /members/${memberId}/home`, json);
  } catch (err) {
    log("GET /members/{id}/home ERROR", err.response || err.message);
  }
});

document
.getElementById("form-mypage")
.addEventListener("submit", async (e) => {
  e.preventDefault();
  const memberId = resolveMemberId(e.target.memberId.value);
  if (!memberId) {
    return alert("Member ID가 없습니다.");
  }
  try {
    const json = await apiCall("GET", `/members/${memberId}/home/mypage`);
    log(`GET /members/${memberId}/home/mypage`, json);
  } catch (err) {
    log("GET /members/{id}/home/mypage ERROR", err.response || err.message);
  }
});

// ======================
// 4. 계좌
// ======================
document
.getElementById("form-account-create")
.addEventListener("submit", async (e) => {
  e.preventDefault();
  const raw = formToObject(e.target);
  const memberId = resolveMemberId(raw.memberId);
  if (!memberId) {
    return alert("Member ID가 없습니다.");
  }

  const body = {
    accountNumber: raw.accountNumber,
    bankName: raw.bankName,
    isPrimary: !!raw.isPrimary,
  };

  try {
    const json = await apiCall(
        "POST",
        `/members/${memberId}/accounts`,
        body
    );
    log(`POST /members/${memberId}/accounts`, json);
    if (json.data && json.data.id) {
      setCurrentAccount(json.data.id);
    }
  } catch (err) {
    log("POST /members/{id}/accounts ERROR", err.response || err.message);
  }
});

document
.getElementById("form-account-list")
.addEventListener("submit", async (e) => {
  e.preventDefault();
  const memberId = resolveMemberId(e.target.memberId.value);
  if (!memberId) {
    return alert("Member ID가 없습니다.");
  }

  try {
    const json = await apiCall("GET", `/members/${memberId}/accounts`);
    log(`GET /members/${memberId}/accounts`, json);
    renderAccountList(json.data || []);
  } catch (err) {
    log("GET /members/{id}/accounts ERROR", err.response || err.message);
  }
});

function renderAccountList(list) {
  const wrap = document.getElementById("account-list");
  if (!list || list.length === 0) {
    wrap.innerHTML = "<div class='empty'>계좌가 없습니다.</div>";
    return;
  }
  const headers = ["id", "bankName", "accountNumber", "balance", "primary"];
  let html = "<table><thead><tr>";
  headers.forEach((h) => (html += `<th>${h}</th>`));
  html += "</tr></thead><tbody>";
  list.forEach((a) => {
    html += `<tr data-id="${a.id}">
          <td>${a.id}</td>
          <td>${a.bankName || ""}</td>
          <td>${a.accountNumber || ""}</td>
          <td>${a.balance ?? ""}</td>
          <td>${a.primary ? "Y" : ""}</td>
        </tr>`;
  });
  html += "</tbody></table>";
  wrap.innerHTML = html;
  wrap.querySelectorAll("tr[data-id]").forEach((tr) => {
    tr.addEventListener("click", () => {
      const id = tr.dataset.id;
      setCurrentAccount(id);
    });
  });
}

document
.getElementById("form-account-detail")
.addEventListener("submit", async (e) => {
  e.preventDefault();
  const raw = formToObject(e.target);
  const memberId = resolveMemberId(raw.memberId);
  const accountId = raw.accountId || currentAccountId;
  if (!memberId || !accountId) {
    return alert("Member ID / Account ID가 필요합니다.");
  }
  try {
    const json = await apiCall(
        "GET",
        `/members/${memberId}/accounts/${accountId}`
    );
    log(
        `GET /members/${memberId}/accounts/${accountId}`,
        json
    );
    if (json.data && json.data.id) {
      setCurrentAccount(json.data.id);
    }
  } catch (err) {
    log(
        "GET /members/{id}/accounts/{accountId} ERROR",
        err.response || err.message
    );
  }
});

document
.getElementById("form-account-primary")
.addEventListener("submit", async (e) => {
  e.preventDefault();
  const raw = formToObject(e.target);
  const memberId = resolveMemberId(raw.memberId);
  const accountId = raw.accountId || currentAccountId;
  if (!memberId || !accountId) {
    return alert("Member ID / Account ID가 필요합니다.");
  }
  try {
    const json = await apiCall(
        "PUT",
        `/members/${memberId}/accounts/${accountId}/primary`
    );
    log(
        `PUT /members/${memberId}/accounts/${accountId}/primary`,
        json
    );
  } catch (err) {
    log(
        "PUT /members/{id}/accounts/{accountId}/primary ERROR",
        err.response || err.message
    );
  }
});

document
.getElementById("form-account-delete")
.addEventListener("submit", async (e) => {
  e.preventDefault();
  const raw = formToObject(e.target);
  const memberId = resolveMemberId(raw.memberId);
  const accountId = raw.accountId || currentAccountId;
  if (!memberId || !accountId) {
    return alert("Member ID / Account ID가 필요합니다.");
  }
  if (!confirm("정말 계좌를 삭제(비활성화) 하시겠습니까?")) {
    return;
  }
  try {
    const json = await apiCall(
        "DELETE",
        `/members/${memberId}/accounts/${accountId}`
    );
    log(
        `DELETE /members/${memberId}/accounts/${accountId}`,
        json
    );
  } catch (err) {
    log(
        "DELETE /members/{id}/accounts/{accountId} ERROR",
        err.response || err.message
    );
  }
});

document
.getElementById("form-account-deposit")
.addEventListener("submit", async (e) => {
  e.preventDefault();
  const raw = formToObject(e.target);
  const memberId = resolveMemberId(raw.memberId);
  const accountId = raw.accountId || currentAccountId;
  if (!memberId || !accountId) {
    return alert("Member ID / Account ID가 필요합니다.");
  }
  const body = {
    amount: raw.amount,
    description: raw.description || null,
  };
  try {
    const json = await apiCall(
        "POST",
        `/members/${memberId}/accounts/${accountId}/deposit`,
        body
    );
    log(
        `POST /members/${memberId}/accounts/${accountId}/deposit`,
        json
    );
  } catch (err) {
    log(
        "POST /members/{id}/accounts/{accountId}/deposit ERROR",
        err.response || err.message
    );
  }
});

document
.getElementById("form-account-withdraw")
.addEventListener("submit", async (e) => {
  e.preventDefault();
  const raw = formToObject(e.target);
  const memberId = resolveMemberId(raw.memberId);
  const accountId = raw.accountId || currentAccountId;
  if (!memberId || !accountId) {
    return alert("Member ID / Account ID가 필요합니다.");
  }
  const body = {
    amount: raw.amount,
    description: raw.description || null,
  };
  try {
    const json = await apiCall(
        "POST",
        `/members/${memberId}/accounts/${accountId}/withdraw`,
        body
    );
    log(
        `POST /members/${memberId}/accounts/${accountId}/withdraw`,
        json
    );
  } catch (err) {
    log(
        "POST /members/{id}/accounts/{accountId}/withdraw ERROR",
        err.response || err.message
    );
  }
});

// ======================
// 5. 티끌통장
// ======================
document
.getElementById("form-savings-get")
.addEventListener("submit", async (e) => {
  e.preventDefault();
  const memberId = resolveMemberId(e.target.memberId.value);
  if (!memberId) {
    return alert("Member ID가 없습니다.");
  }
  try {
    const json = await apiCall("GET", `/members/${memberId}/savings`);
    log(`GET /members/${memberId}/savings`, json);
  } catch (err) {
    log("GET /members/{id}/savings ERROR", err.response || err.message);
  }
});

document
.getElementById("form-savings-link")
.addEventListener("submit", async (e) => {
  e.preventDefault();
  const raw = formToObject(e.target);
  const memberId = resolveMemberId(raw.memberId);
  if (!memberId) {
    return alert("Member ID가 없습니다.");
  }
  const body = {productId: raw.productId};
  try {
    const json = await apiCall(
        "PUT",
        `/members/${memberId}/savings/product`,
        body
    );
    log(
        `PUT /members/${memberId}/savings/product`,
        json
    );
  } catch (err) {
    log(
        "PUT /members/{id}/savings/product ERROR",
        err.response || err.message
    );
  }
});

// ======================
// 6. 금융상품
// ======================
document
.getElementById("form-products-all")
.addEventListener("submit", async (e) => {
  e.preventDefault();
  try {
    const json = await apiCall("GET", "/api/products");
    log("GET /api/products", json);
  } catch (err) {
    log("GET /api/products ERROR", err.response || err.message);
  }
});

document
.getElementById("form-products-type")
.addEventListener("submit", async (e) => {
  e.preventDefault();
  const type = e.target.productType.value;
  try {
    const json = await apiCall("GET", `/api/products/type/${type}`);
    log(`GET /api/products/type/${type}`, json);
  } catch (err) {
    log("GET /api/products/type/{type} ERROR", err.response || err.message);
  }
});

document
.getElementById("form-products-one")
.addEventListener("submit", async (e) => {
  e.preventDefault();
  const id = e.target.productId.value;
  if (!id) {
    return;
  }
  try {
    const json = await apiCall("GET", `/api/products/${id}`);
    log(`GET /api/products/${id}`, json);
  } catch (err) {
    log("GET /api/products/{id} ERROR", err.response || err.message);
  }
});

document
.getElementById("form-products-create")
.addEventListener("submit", async (e) => {
  e.preventDefault();
  const body = formToObject(e.target);
  try {
    const json = await apiCall("POST", "/api/products", body);
    log("POST /api/products", json);
  } catch (err) {
    log("POST /api/products ERROR", err.response || err.message);
  }
});

// ======================
// 7. 마스터카드
// ======================
document
.getElementById("form-cardproducts-all")
.addEventListener("submit", async (e) => {
  e.preventDefault();
  try {
    const json = await apiCall("GET", "/card-products");
    log("GET /card-products", json);
  } catch (err) {
    log("GET /card-products ERROR", err.response || err.message);
  }
});

document
.getElementById("form-cardproducts-one")
.addEventListener("submit", async (e) => {
  e.preventDefault();
  const id = e.target.id.value;
  if (!id) {
    return;
  }
  try {
    const json = await apiCall("GET", `/card-products/${id}`);
    log(`GET /card-products/${id}`, json);
  } catch (err) {
    log("GET /card-products/{id} ERROR", err.response || err.message);
  }
});

document
.getElementById("form-cardproducts-create")
.addEventListener("submit", async (e) => {
  e.preventDefault();
  const body = formToObject(e.target);
  try {
    const json = await apiCall("POST", "/card-products", body);
    log("POST /card-products", json);
  } catch (err) {
    log("POST /card-products ERROR", err.response || err.message);
  }
});

document
.getElementById("form-benefit-add")
.addEventListener("submit", async (e) => {
  e.preventDefault();
  const raw = formToObject(e.target);
  const id = raw.productId;
  if (!id) {
    return alert("CardProduct ID가 필요합니다.");
  }
  const body = {
    benefitName: raw.benefitName,
    benefitType: raw.benefitType,
    categoryCode: raw.categoryCode,
    discountRate: raw.discountRate,
    maxDiscountPerMonth: raw.maxDiscountPerMonth,
    minSpendingForActivation: raw.minSpendingForActivation,
    description: raw.description,
  };
  try {
    const json = await apiCall(
        "POST",
        `/card-products/${id}/benefits`,
        body
    );
    log(`POST /card-products/${id}/benefits`, json);
  } catch (err) {
    log(
        "POST /card-products/{id}/benefits ERROR",
        err.response || err.message
    );
  }
});

document
.getElementById("form-benefit-list")
.addEventListener("submit", async (e) => {
  e.preventDefault();
  const id = e.target.productId.value;
  if (!id) {
    return;
  }
  try {
    const json = await apiCall("GET", `/card-products/${id}/benefits`);
    log(`GET /card-products/${id}/benefits`, json);
  } catch (err) {
    log(
        "GET /card-products/{id}/benefits ERROR",
        err.response || err.message
    );
  }
});

// ======================
// 8. 카드
// ======================
document
.getElementById("form-card-create")
.addEventListener("submit", async (e) => {
  e.preventDefault();
  const raw = formToObject(e.target);
  const memberId = resolveMemberId(raw.memberId);
  if (!memberId) {
    return alert("Member ID가 없습니다.");
  }
  const params = new URLSearchParams();
  if (raw.productId) {
    params.set("productId", raw.productId);
  }
  if (raw.accountId) {
    params.set("accountId", raw.accountId);
  }
  if (raw.nickname) {
    params.set("nickname", raw.nickname);
  }

  const url = `/members/${memberId}/cards?` + params.toString();
  try {
    const json = await apiCall("POST", url);
    log(`POST ${url}`, json);
    if (json.data) {
      setCurrentCard(json.data);
    }
  } catch (err) {
    log("POST /members/{id}/cards ERROR", err.response || err.message);
  }
});

document
.getElementById("form-card-list")
.addEventListener("submit", async (e) => {
  e.preventDefault();
  const memberId = resolveMemberId(e.target.memberId.value);
  if (!memberId) {
    return alert("Member ID가 없습니다.");
  }
  try {
    const json = await apiCall("GET", `/members/${memberId}/cards`);
    log(`GET /members/${memberId}/cards`, json);
    renderCardList(json.data || []);
  } catch (err) {
    log("GET /members/{id}/cards ERROR", err.response || err.message);
  }
});

function renderCardList(list) {
  const wrap = document.getElementById("card-list");
  if (!list || list.length === 0) {
    wrap.innerHTML = "<div class='empty'>카드가 없습니다.</div>";
    return;
  }
  const headers = ["id", "cardName", "cardNumber", "company",
    "currentMonthSpending"];
  let html = "<table><thead><tr>";
  headers.forEach((h) => (html += `<th>${h}</th>`));
  html += "</tr></thead><tbody>";
  list.forEach((c) => {
    html += `<tr data-id="${c.id}">
          <td>${c.id}</td>
          <td>${c.cardName || ""}</td>
          <td>${c.cardNumber || ""}</td>
          <td>${c.company || ""}</td>
          <td>${c.currentMonthSpending ?? ""}</td>
        </tr>`;
  });
  html += "</tbody></table>";
  wrap.innerHTML = html;
  wrap.querySelectorAll("tr[data-id]").forEach((tr) => {
    tr.addEventListener("click", () => {
      const id = tr.dataset.id;
      setCurrentCard(id);
    });
  });
}

document
.getElementById("form-card-detail")
.addEventListener("submit", async (e) => {
  e.preventDefault();
  const raw = formToObject(e.target);
  const memberId = resolveMemberId(raw.memberId);
  const cardId = raw.cardId || currentCardId;
  if (!memberId || !cardId) {
    return alert("Member ID / Card ID 필요");
  }
  try {
    const json = await apiCall(
        "GET",
        `/members/${memberId}/cards/${cardId}`
    );
    log(`GET /members/${memberId}/cards/${cardId}`, json);
  } catch (err) {
    log(
        "GET /members/{id}/cards/{cardId} ERROR",
        err.response || err.message
    );
  }
});

document
.getElementById("form-card-payment")
.addEventListener("submit", async (e) => {
  e.preventDefault();
  const raw = formToObject(e.target);
  const memberId = resolveMemberId(raw.memberId);
  const cardId = raw.cardId || currentCardId;
  if (!memberId || !cardId) {
    return alert("Member ID / Card ID 필요");
  }
  const body = {
    amount: raw.amount,
    merchant: raw.merchant,
    category: raw.category,
    description: raw.description || null,
  };
  try {
    const json = await apiCall(
        "POST",
        `/members/${memberId}/cards/${cardId}/payments`,
        body
    );
    log(
        `POST /members/${memberId}/cards/${cardId}/payments`,
        json
    );
  } catch (err) {
    log(
        "POST /members/{id}/cards/{cardId}/payments ERROR",
        err.response || err.message
    );
  }
});

document
.getElementById("form-card-set-account")
.addEventListener("submit", async (e) => {
  e.preventDefault();
  const raw = formToObject(e.target);
  const memberId = resolveMemberId(raw.memberId);
  const cardId = raw.cardId || currentCardId;
  if (!memberId || !cardId) {
    return alert("Member ID / Card ID 필요");
  }
  const body = {accountId: raw.accountId};
  try {
    const json = await apiCall(
        "PUT",
        `/members/${memberId}/cards/${cardId}/payment-account`,
        body
    );
    log(
        `PUT /members/${memberId}/cards/${cardId}/payment-account`,
        json
    );
  } catch (err) {
    log(
        "PUT /members/{id}/cards/{cardId}/payment-account ERROR",
        err.response || err.message
    );
  }
});

document
.getElementById("form-card-deactivate")
.addEventListener("submit", async (e) => {
  e.preventDefault();
  const raw = formToObject(e.target);
  const memberId = resolveMemberId(raw.memberId);
  const cardId = raw.cardId || currentCardId;
  if (!memberId || !cardId) {
    return alert("Member ID / Card ID 필요");
  }
  if (!confirm("정말 카드를 비활성화(해지) 하시겠습니까?")) {
    return;
  }
  try {
    const json = await apiCall(
        "DELETE",
        `/members/${memberId}/cards/${cardId}`
    );
    log(
        `DELETE /members/${memberId}/cards/${cardId}`,
        json
    );
  } catch (err) {
    log(
        "DELETE /members/{id}/cards/{cardId} ERROR",
        err.response || err.message
    );
  }
});

// ======================
// 9. 거래내역
// ======================
document
.getElementById("form-tx-recent")
.addEventListener("submit", async (e) => {
  e.preventDefault();
  const memberId = resolveMemberId(e.target.memberId.value);
  if (!memberId) {
    return alert("Member ID가 없습니다.");
  }
  try {
    const json = await apiCall(
        "GET",
        `/members/${memberId}/transactions/recent`
    );
    log(
        `GET /members/${memberId}/transactions/recent`,
        json
    );
  } catch (err) {
    log(
        "GET /members/{id}/transactions/recent ERROR",
        err.response || err.message
    );
  }
});

document
.getElementById("form-tx-search")
.addEventListener("submit", async (e) => {
  e.preventDefault();
  const raw = formToObject(e.target);
  const memberId = resolveMemberId(raw.memberId);
  if (!memberId) {
    return alert("Member ID가 없습니다.");
  }
  const body = {
    transactionType: raw.transactionType || null,
    category: raw.category || null,
    startDate: raw.startDate || null,
    endDate: raw.endDate || null,
    keyword: raw.keyword || null,
    page: raw.page ?? 0,
    size: raw.size ?? 20,
  };
  try {
    const json = await apiCall(
        "POST",
        `/members/${memberId}/transactions/search`,
        body
    );
    log(
        `POST /members/${memberId}/transactions/search`,
        json
    );
  } catch (err) {
    log(
        "POST /members/{id}/transactions/search ERROR",
        err.response || err.message
    );
  }
});

document
.getElementById("form-tx-detail")
.addEventListener("submit", async (e) => {
  e.preventDefault();
  const raw = formToObject(e.target);
  const memberId = resolveMemberId(raw.memberId);
  const txId = raw.transactionId;
  if (!memberId || !txId) {
    return alert("Member ID / Transaction ID 필요");
  }
  try {
    const json = await apiCall(
        "GET",
        `/members/${memberId}/transactions/${txId}`
    );
    log(
        `GET /members/${memberId}/transactions/${txId}`,
        json
    );
  } catch (err) {
    log(
        "GET /members/{id}/transactions/{txId} ERROR",
        err.response || err.message
    );
  }
});

// ======================
// 10. 시뮬레이션
// ======================
document
.getElementById("form-sim-day")
.addEventListener("submit", async (e) => {
  e.preventDefault();
  try {
    const json = await apiCall("POST", "/api/dev/simulate/day");
    log("POST /api/dev/simulate/day", json);
  } catch (err) {
    log(
        "POST /api/dev/simulate/day ERROR",
        err.response || err.message
    );
  }
});

document
.getElementById("form-sim-month")
.addEventListener("submit", async (e) => {
  e.preventDefault();
  try {
    const json = await apiCall("POST", "/api/dev/simulate/month-end");
    log("POST /api/dev/simulate/month-end", json);
  } catch (err) {
    log(
        "POST /api/dev/simulate/month-end ERROR",
        err.response || err.message
    );
  }
});