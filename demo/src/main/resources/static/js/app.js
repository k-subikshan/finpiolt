const API = "/api";

const USER = "subi";
const MONTH = "2029-09";
async function loadDashboard() {
    try {
        const response = await fetch(
            `${API}/dashboard?user=${USER}&month=${MONTH}`
        );

        if (!response.ok) {
            throw new Error("Failed to load dashboard");
        }

        const data = await response.json();

        console.log("Dashboard data:", data);

        document.getElementById("totalIncome").textContent =
            `₹${Number(data.totalIncome).toLocaleString("en-IN")}`;

        document.getElementById("totalExpenses").textContent =
            `₹${Number(data.totalExpenses).toLocaleString("en-IN")}`;

        document.getElementById("savings").textContent =
            `₹${Number(data.savings).toLocaleString("en-IN")}`;

        document.getElementById("transactionCount").textContent =
            data.transactionCount;

    } catch (error) {
        console.error("Dashboard error:", error);
    }
}

/* ================= NAVIGATION ================= */

function showPage(pageId, button) {

    document.querySelectorAll(".page")
        .forEach(page => page.classList.remove("active"));

    const page = document.getElementById(pageId);

    if (page) {
        page.classList.add("active");
    }


    document.querySelectorAll(".nav-item")
        .forEach(item => item.classList.remove("active"));

    if (button) {
        button.classList.add("active");
    }


    const titles = {

        dashboard: "Good evening, Subi.",

        transactions: "Your transactions.",

        budgets: "Your monthly budgets.",

        subscriptions: "Your recurring payments.",

        insights: "Your financial insights.",

        goals: "Your financial goals."

    };


    document.getElementById("pageTitle").textContent =
        titles[pageId] || "FinPilot";
}


function showPageByName(pageId) {

    const button = [...document.querySelectorAll(".nav-item")]
        .find(button =>
            button.getAttribute("onclick")?.includes(pageId)
        );

    showPage(pageId, button);
}


/* ================= DASHBOARD ================= */

async function loadDashboard() {

    try {

        const response =
            await fetch(
                `${API}/dashboard?user=${USER}&month=${MONTH}`
            );


        if (!response.ok) {

            throw new Error("Dashboard API failed");

        }


        const data = await response.json();


        document.getElementById("income").textContent =
            money(data.totalIncome);


        document.getElementById("expenses").textContent =
            money(data.totalExpenses);


        document.getElementById("savings").textContent =
            money(data.savings);


        document.getElementById("transactionCount").textContent =
            data.transactionCount ?? 0;


        renderCategories(
            data.categoryBreakdown || []
        );


    } catch (error) {

        console.error(error);

        console.log(
            "Spring Boot dashboard endpoint is not available yet."
        );

    }
}


/* ================= CATEGORY ================= */

function renderCategories(categories) {

    const container =
        document.getElementById("categoryChart");


    if (!categories.length) {

        container.innerHTML =
            `<p class="muted">No spending data available.</p>`;

        return;
    }


    const max =
        Math.max(
            ...categories.map(item => Number(item.amount))
        );


    container.innerHTML =
        categories.map(item => {

            const percentage =
                max > 0
                    ? (Number(item.amount) / max) * 100
                    : 0;


            return `

                <div class="category-row">

                    <span class="category-name">
                        ${escapeHtml(item.category)}
                    </span>

                    <div class="category-bar">

                        <div style="width:${percentage}%">
                        </div>

                    </div>

                    <span class="category-value">
                        ${money(item.amount)}
                    </span>

                </div>

            `;

        }).join("");
}


/* ================= TRANSACTIONS ================= */

async function loadTransactions() {

    try {

        const response =
            await fetch(
                `${API}/transactions?user=${USER}`
            );


        if (!response.ok) {

            throw new Error("Transactions API failed");

        }


        const transactions =
            await response.json();


        const tbody =
            document.getElementById(
                "transactionTable"
            );


        if (!transactions.length) {

            tbody.innerHTML = `

                <tr>
                    <td colspan="5">
                        No transactions found.
                    </td>
                </tr>

            `;

            return;
        }


        tbody.innerHTML =
            transactions.map(transaction => {

                const type =
                    transaction.transaction_type;


                return `

                    <tr>

                        <td>
                            ${escapeHtml(
                                transaction.transaction_date
                            )}
                        </td>

                        <td>
                            ${escapeHtml(
                                transaction.merchant
                            )}
                        </td>

                        <td>
                            ${escapeHtml(
                                transaction.category || "Other"
                            )}
                        </td>

                        <td>

                            <span class="badge expense">
                                ${escapeHtml(type)}
                            </span>

                        </td>

                        <td>
                            ${money(transaction.amount)}
                        </td>

                    </tr>

                `;

            }).join("");


    } catch (error) {

        console.error(error);

    }
}


/* ================= BUDGETS ================= */

/* ================= BUDGETS ================= */

async function loadBudgets() {
    try {
        const response = await fetch(
            `${API}/budgets?user=${USER}`
        );

        if (!response.ok) {
            throw new Error("Failed to load budgets");
        }

        const budgets = await response.json();

        console.log("Budgets received:", budgets);

        renderBudgets(budgets);

    } catch (error) {
        console.error("Budget loading error:", error);
    }
}


function getBudgetContainer() {
    let container = document.getElementById("budgetContainer");

    if (container) {
        return container;
    }

    const budgetsPage =
        document.getElementById("budgets") ||
        document.getElementById("budgetsPage") ||
        document.querySelector('[data-page="budgets"]');

    if (!budgetsPage) {
        console.error(
            'Budget page/container not found. Add <div id="budgetContainer"></div> to the Budgets page.'
        );
        return null;
    }

    container = document.createElement("div");
    container.id = "budgetContainer";
    container.className = "budget-grid";

    budgetsPage.appendChild(container);

    return container;
}


function renderBudgets(budgets) {

    const container = getBudgetContainer();

    if (!container) {
        return;
    }

    container.innerHTML = "";

    if (!Array.isArray(budgets) || budgets.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                No budgets found.
            </div>
        `;
        return;
    }

    budgets.forEach(budget => {

        const limit = Number(budget.monthly_limit) || 0;
        const spent = Number(budget.spent) || 0;

        const percentage =
            limit > 0
                ? Math.min((spent / limit) * 100, 100)
                : 0;

        const remaining = Math.max(limit - spent, 0);

        const card = document.createElement("div");

        card.className = "budget-card";

        card.innerHTML = `
            <div class="budget-header">

                <div>
                    <span class="budget-category">
                        ${escapeHtml(budget.category)}
                    </span>

                    <div class="budget-amount">
                        ${money(spent)} / ${money(limit)}
                    </div>
                </div>

                <div class="budget-edit">

                    <label for="budget-${budget.id}">
                        Monthly limit
                    </label>

                    <div class="budget-edit-row">

                        <span>₹</span>

                        <input
                            type="number"
                            min="0"
                            step="100"
                            value="${limit}"
                            id="budget-${budget.id}"
                            aria-label="Monthly budget for ${escapeHtml(budget.category)}"
                        >

                        <button
                            type="button"
                            onclick="saveBudget(${budget.id})"
                        >
                            Save
                        </button>

                    </div>

                </div>

            </div>

            <div class="budget-progress">

                <div
                    class="budget-progress-fill"
                    style="width:${percentage}%"
                ></div>

            </div>

            <div class="budget-footer">

                <span>
                    ${percentage.toFixed(0)}% used
                </span>

                <span>
                    ${money(remaining)} remaining
                </span>

            </div>
        `;

        container.appendChild(card);
    });
}


async function saveBudget(id) {

    const input =
        document.getElementById(`budget-${id}`);

    if (!input) {
        console.error(
            `Budget input not found for id ${id}`
        );
        return;
    }

    const monthlyLimit =
        Number(input.value);

    if (
        !Number.isFinite(monthlyLimit) ||
        monthlyLimit < 0
    ) {
        alert("Please enter a valid budget.");
        input.focus();
        return;
    }

    try {

        const response = await fetch(
            `${API}/budgets/${id}`,
            {
                method: "PUT",

                headers: {
                    "Content-Type":
                        "application/json"
                },

                body: JSON.stringify({
                    monthly_limit:
                        monthlyLimit
                })
            }
        );

        const result =
            await response.json();

        if (!response.ok) {

            throw new Error(
                result.error ||
                "Unable to update budget"
            );
        }

        await loadBudgets();

    } catch (error) {

        console.error(
            "Budget update error:",
            error
        );

        alert(
            "Failed to update budget: " +
            error.message
        );
    }
}
/* ================= GOALS ================= */

async function loadGoals() {

    try {

        const response =
            await fetch(
                `${API}/goals?user=${USER}`
            );


        if (!response.ok) {

            throw new Error("Goal API failed");

        }


        const goals =
            await response.json();


        if (!goals.length) {

            document.getElementById("goalDetails")
                .textContent =
                "No savings goal configured.";

            return;
        }


        const goal = goals[0];


        const target =
            Number(goal.target_amount || 0);

        const current =
            Number(goal.current_amount || 0);


        const percent =
            target > 0
                ? Math.min(
                    (current / target) * 100,
                    100
                )
                : 0;


        document.getElementById("goalName")
            .textContent =
            goal.goal_name;


        document.getElementById("goalCurrent")
            .textContent =
            money(current);


        document.getElementById("goalPercent")
            .textContent =
            `${percent.toFixed(0)}%`;


        document.getElementById("goalProgress")
            .style.width =
            `${percent}%`;


        document.getElementById("goalDetails")
            .textContent =
            `${money(
                Math.max(target - current, 0)
            )} remaining of ${money(target)}`;


        document.getElementById("goalsPage")
            .innerHTML = `

                <div class="panel">

                    <span class="panel-label">
                        ${escapeHtml(goal.goal_name)}
                    </span>

                    <h2 style="margin:12px 0">
                        ${money(current)}
                    </h2>

                    <p class="muted">
                        Target: ${money(target)}
                        ·
                        ${escapeHtml(
                            goal.target_date || ""
                        )}
                    </p>

                    <br>

                    <div class="progress">

                        <div style="width:${percent}%">
                        </div>

                    </div>

                </div>

            `;


    } catch (error) {

        console.error(error);

    }
}


/* ================= SUBSCRIPTIONS ================= */

async function loadSubscriptions() {

    try {

        const response =
            await fetch(
                `${API}/subscriptions?user=${USER}`
            );


        if (!response.ok) {

            throw new Error(
                "Subscriptions API failed"
            );

        }


        const subscriptions =
            await response.json();


        const container =
            document.getElementById(
                "subscriptionContainer"
            );


        container.innerHTML =
            subscriptions.map(item => `

                <div class="subscription-card">

                    <div class="service-icon">
                        ${escapeHtml(
                            (item.merchant || "S")[0]
                        )}
                    </div>

                    <div>

                        <strong>
                            ${escapeHtml(
                                item.merchant
                            )}
                        </strong>

                        <small>
                            ${escapeHtml(
                                item.type || "Recurring"
                            )}
                        </small>

                    </div>

                    <div class="subscription-price">
                        ${money(item.amount)}
                    </div>

                </div>

            `).join("");


    } catch (error) {

        console.error(error);

    }
}


/* ================= AI ASK ================= */

function openAsk() {

    document.getElementById("askModal")
        .classList.add("show");
}


function closeAsk() {

    document.getElementById("askModal")
        .classList.remove("show");
}


async function askFinPilot() {

    const question =
        document.getElementById("question")
            .value.trim();


    const answer =
        document.getElementById("answer");


    if (!question) {

        answer.textContent =
            "Please enter a question.";

        return;
    }


    answer.textContent =
        "FinPilot is analyzing your financial data...";


    try {

        const response =
            await fetch(
                `${API}/finance/ask`,
                {

                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body: JSON.stringify({

                        user_name: USER,

                        question: question,

                        month: MONTH

                    })

                }
            );


        if (!response.ok) {

            throw new Error(
                "Finance Q&A API failed"
            );
        }


        const data =
            await response.json();


        answer.innerHTML = `

            <strong>
                ${escapeHtml(
                    data.answer || "No answer available."
                )}
            </strong>

            ${
                data.key_facts?.length
                    ? `

                        <br><br>

                        <strong>
                            Key facts
                        </strong>

                        <ul>

                            ${data.key_facts.map(
                                fact =>
                                    `<li>
                                        ${escapeHtml(fact)}
                                    </li>`
                            ).join("")}

                        </ul>

                    `
                    : ""
            }

        `;


    } catch (error) {

        console.error(error);

        answer.textContent =
            "Unable to connect to FinPilot.";
    }
}


/* ================= UPLOAD ================= */

function openUpload() {

    document.getElementById("uploadModal")
        .classList.add("show");
}


function closeUpload() {

    document.getElementById("uploadModal")
        .classList.remove("show");
}


async function uploadStatement() {

    const file =
        document.getElementById(
            "statementFile"
        ).files[0];


    const status =
        document.getElementById(
            "uploadStatus"
        );


    if (!file) {

        status.textContent =
            "Please select a CSV file.";

        return;
    }


    const formData =
        new FormData();


    formData.append(
        "file",
        file
    );


    formData.append(
        "user_name",
        USER
    );


    status.textContent =
        "Uploading and processing...";


    try {

        const response =
            await fetch(
                `${API}/transactions/upload`,
                {

                    method: "POST",

                    body: formData

                }
            );


        if (!response.ok) {

            throw new Error(
                "Upload failed"
            );
        }


        const data =
            await response.json();


        status.textContent =
            data.message ||
            "Statement uploaded successfully.";

        loadDashboard();
        loadTransactions();


    } catch (error) {

        console.error(error);

        status.textContent =
            "Upload failed. Please try again.";
    }
}


/* ================= THEME ================= */

function toggleTheme() {

    document.body.classList.toggle("dark");

}


/* ================= HELPERS ================= */

function money(value) {

    const number =
        Number(value || 0);


    return number.toLocaleString(
        "en-IN",
        {
            style: "currency",
            currency: "INR",
            maximumFractionDigits: 0
        }
    );
}


function escapeHtml(value) {

    return String(value ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}


/* ================= START ================= */

document.addEventListener(
    "DOMContentLoaded",
    () => {

        loadDashboard();

        loadTransactions();

        loadBudgets();

        loadGoals();

        loadSubscriptions();

    }
);
document.addEventListener("DOMContentLoaded", loadDashboard);
document.addEventListener("DOMContentLoaded", loadDashboard);

async function uploadStatement() {

    const fileInput = document.getElementById("transactionCsvFile");

    if (!fileInput) {
        alert("File input not found.");
        return;
    }

    if (fileInput.files.length === 0) {
        alert("Please select a CSV file.");
        return;
    }

    const file = fileInput.files[0];

    console.log("Selected file:", file.name);

    const formData = new FormData();
    formData.append("file", file);
    formData.append("user_name", "subi");

    try {
        const response = await fetch("/api/transactions/upload", {
            method: "POST",
            body: formData
        });

        const result = await response.text();

        console.log("Server response:", result);

        if (!response.ok) {
            throw new Error(result);
        }

        alert("Statement uploaded successfully!");

    } catch (error) {
        console.error("Upload error:", error);
        alert("Upload failed: " + error.message);
    }
}