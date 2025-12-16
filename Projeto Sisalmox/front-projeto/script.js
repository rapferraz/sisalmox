const API_URL = "http://localhost:8080/api/produtos";

let produtos = [];

let myChart = null;
let chartVolumeObj = null;
let chartFluxoObj = null;

/* ===========================
   INIT
=========================== */
document.addEventListener("DOMContentLoaded", carregarProdutos);

/* ===========================
   TOAST
=========================== */
function showToast(message, type = "success") {
    const container = document.getElementById("toast-container");
    const toast = document.createElement("div");
    toast.className = `toast ${type}`;
    const icon = type === "success" ? "fa-check-circle" : "fa-exclamation-circle";
    toast.innerHTML = `<i class="fas ${icon}"></i><span>${message}</span>`;
    container.appendChild(toast);
    setTimeout(() => toast.remove(), 3500);
}

/* ===========================
   BACKEND
=========================== */
function carregarProdutos() {
    fetch(API_URL)
        .then(res => {
            if (!res.ok) throw new Error("Erro ao carregar produtos");
            return res.json();
        })
        .then(data => {
            produtos = data;
            atualizarDashboard();
            renderizarTabela(produtos);
            if (document.getElementById("modalRelatorios")?.classList.contains("active")) {
                gerarRelatorios();
            }
        })
        .catch(err => {
            console.error(err);
            showToast("Erro ao carregar produtos", "error");
        });
}

/* ===========================
   DASHBOARD
=========================== */
function atualizarDashboard() {
    document.getElementById("total-itens-display").innerText = produtos.length;

    const categorias = {};
    produtos.forEach(p => {
        categorias[p.categoria] = (categorias[p.categoria] || 0) + 1;
    });

    const ctx = document.getElementById("estoqueChart").getContext("2d");
    if (myChart) myChart.destroy();

    myChart = new Chart(ctx, {
        type: "doughnut",
        data: {
            labels: Object.keys(categorias),
            datasets: [{
                data: Object.values(categorias),
                backgroundColor: ["#38bdf8", "#818cf8", "#34d399", "#f472b6", "#facc15"],
                borderWidth: 0
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: "right",
                    labels: { color: "#94a3b8", font: { size: 10 } }
                }
            }
        }
    });
}

/* ===========================
   RELATÓRIOS
=========================== */
function gerarRelatorios() {
    const totaisQtd = {};
    let totalUnidades = 0;

    produtos.forEach(p => {
        totaisQtd[p.categoria] = (totaisQtd[p.categoria] || 0) + p.quantidade;
        totalUnidades += p.quantidade;
    });

    document.getElementById("kpi-total-unidades").innerText = `${totalUnidades} un.`;

    const catDominante = Object.keys(totaisQtd)
        .reduce((a, b) => totaisQtd[a] > totaisQtd[b] ? a : b, "-");

    document.getElementById("kpi-cat-dominante").innerText = catDominante;

    if (chartVolumeObj) chartVolumeObj.destroy();
    chartVolumeObj = new Chart(document.getElementById("chartVolume"), {
        type: "bar",
        data: {
            labels: Object.keys(totaisQtd),
            datasets: [{
                data: Object.values(totaisQtd),
                backgroundColor: "rgba(56,189,248,.6)"
            }]
        },
        options: { responsive: true, maintainAspectRatio: false }
    });

    if (chartFluxoObj) chartFluxoObj.destroy();
    chartFluxoObj = new Chart(document.getElementById("chartFluxo"), {
        type: "line",
        data: {
            labels: ["Mês 1", "Mês 2", "Mês 3", "Mês 4", "Mês 5", "Atual"],
            datasets: [
                { label: "Entradas", data: [15,22,10,45,30,60], borderColor: "#10b981" },
                { label: "Saídas", data: [10,18,15,20,25,40], borderColor: "#ef4444" }
            ]
        },
        options: { responsive: true, maintainAspectRatio: false }
    });
}

/* ===========================
   MODAIS
=========================== */
function abrirModalEstoque() {
    document.getElementById("modalEstoque").classList.add("active");
    renderizarTabela(produtos);
}
function fecharModalEstoque() {
    document.getElementById("modalEstoque").classList.remove("active");
}
function abrirModalCadastro() {
    document.getElementById("modalCadastro").classList.add("active");
}
function fecharModalCadastro() {
    document.getElementById("modalCadastro").classList.remove("active");
    document.getElementById("formProduto").reset();
}
function abrirModalRelatorios() {
    document.getElementById("modalRelatorios").classList.add("active");
    gerarRelatorios();
}
function fecharModalRelatorios() {
    document.getElementById("modalRelatorios").classList.remove("active");
}

/* ===========================
   CRUD
=========================== */
function salvarProduto(event) {
    event.preventDefault();

    const produto = {
        nome: document.getElementById("nome").value,
        categoria: document.getElementById("categoria").value,
        quantidade: Number(document.getElementById("quantidade").value)
    };

    fetch(API_URL, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(produto)
    })
        .then(res => res.json())
        .then(data => {
            showToast(`"${data.nome}" cadastrado!`);
            fecharModalCadastro();
            carregarProdutos();
        })
        .catch(() => showToast("Erro ao salvar produto", "error"));
}

function deletarProduto(nome) {
    if (!confirm("Confirma remoção?")) return;

    fetch(`${API_URL}/excluinome/${nome}`, { method: "DELETE" })
        .then(() => {
            showToast("Produto removido");
            carregarProdutos();
        })
        .catch(() => showToast("Erro ao excluir", "error"));
}

function alterarQtd(nome, delta) {
    const endpoint = delta > 0 ? "aumentaqtd" : "baixaqtd";

    fetch(`${API_URL}/${endpoint}/${nome}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ quantidade: Math.abs(delta) })
    })
        .then(() => carregarProdutos())
        .catch(() => showToast("Erro ao atualizar quantidade", "error"));
}

/* ===========================
   BUSCA
=========================== */
function buscarProduto() {
    const termo = document.getElementById("searchInput").value.toLowerCase();
    const categoria = document.getElementById("filterCategoria").value;

    const filtrados = produtos.filter(p =>
        p.nome.toLowerCase().includes(termo) &&
        (categoria === "" || p.categoria === categoria)
    );

    renderizarTabela(filtrados);
}

/* ===========================
   TABELA
=========================== */
function renderizarTabela(lista) {
    const tbody = document.getElementById("tabela-estoque");
    tbody.innerHTML = "";

    if (!lista.length) {
        tbody.innerHTML = `<tr><td colspan="5" style="text-align:center">Nenhum item encontrado</td></tr>`;
        return;
    }

    lista.forEach(p => {
        tbody.innerHTML += `
        <tr>
            <td>#${p.id}</td>
            <td>${p.nome}</td>
            <td>${p.categoria}</td>
            <td>${p.quantidade}</td>
            <td>
                <button onclick="alterarQtd('${p.nome}',1)">+</button>
                <button onclick="alterarQtd('${p.nome}',-1)">-</button>
                <button onclick="deletarProduto('${p.nome}')">🗑</button>
            </td>
        </tr>`;
    });
}