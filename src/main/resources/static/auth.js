function qs(selector, scope = document) {
  return scope.querySelector(selector);
}

function qsa(selector, scope = document) {
  return Array.from(scope.querySelectorAll(selector));
}

const toastEl = document.getElementById("authToast");
const loginForm = document.getElementById("loginForm");
const signupForm = document.getElementById("signupForm");

function showToast(message, type = "success") {
  toastEl.textContent = message;
  toastEl.className = `auth-toast ${type}`;
  toastEl.hidden = false;
  clearTimeout(showToast.timer);
  showToast.timer = setTimeout(() => {
    toastEl.hidden = true;
  }, 3000);
}

async function request(path, options = {}) {
  const response = await fetch(path, {
    method: options.method || "POST",
    headers: {"Content-Type": "application/json"},
    body: JSON.stringify(options.body || {}),
  });
  const json = await response.json().catch(() => ({}));
  if (!response.ok || json.success === false) {
    throw new Error(json.message || "요청에 실패했습니다.");
  }
  return json.data;
}

function switchPanel(target) {
  qsa("[data-auth-tab]").forEach((tab) => {
    tab.classList.toggle("active", tab.dataset.authTab === target);
  });
  qsa("[data-auth-panel]").forEach((panel) => {
    panel.classList.toggle("active", panel.dataset.authPanel === target);
  });
}

qsa("[data-auth-tab]").forEach((tab) => {
  tab.addEventListener("click", () => switchPanel(tab.dataset.authTab));
});

loginForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  const formData = new FormData(loginForm);
  const loginId = formData.get("loginId");
  const password = formData.get("password");
  try {
    const data = await request("/members/login", {
      body: {loginId, password},
    });
    const memberId = data?.id;
    if (memberId) {
      localStorage.setItem("tikklMemberId", memberId);
      sessionStorage.setItem("tikklMemberId", memberId);
    }
    showToast("로그인에 성공했습니다.");
    setTimeout(() => {
      const target = memberId ? `?memberId=${memberId}` : "";
      window.location.href = `tikkl-app.html${target}`;
    }, 800);
  } catch (error) {
    console.error(error);
    showToast(error.message, "error");
  }
});

signupForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  const formData = new FormData(signupForm);
  const payload = Object.fromEntries(formData.entries());
  try {
    const data = await request("/members/signup", {body: payload});
    showToast(`회원가입 완료! ID ${data?.id}로 로그인하세요.`);
    signupForm.reset();
    switchPanel("login");
    if (payload.loginId) {
      qs("[name='loginId']", loginForm).value = payload.loginId;
    }
  } catch (error) {
    console.error(error);
    showToast(error.message, "error");
  }
});
