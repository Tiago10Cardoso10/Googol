var stompClient = null;
let currentPage = 1;
const itemsPerPage = 10;
let searchResults = [];

function passar_url(event) {
    event.preventDefault();
    console.log("testa_url");

    $.ajax({
        url: 'http://localhost:8080/passar_url',
        type: 'POST',
        contentType: 'application/json',
        dataType: 'json',
        data: JSON.stringify($('#url').val().trim()),
        success: function (response) {
            if (response && response.message) {
                alert(response.message);
            } else {
                alert('Resposta do servidor não contém mensagem.');
            }
            $('#url').val('');
        },
        error: function (xhr) {
            alert('Erro ao enviar URL: ' + xhr.status + ': ' + xhr.statusText);
            $('#url').val('');
        }
    });
}

function passar_palavra(event) {
    event.preventDefault();
    console.log("testa_palavra");

    $.ajax({
        url: 'http://localhost:8080/passar_palavra',
        type: 'POST',
        contentType: 'application/json',
        dataType: 'json',
        data: JSON.stringify($('#palavra').val().trim()),
        success: function (response) {
            if (response && response.message) {
                searchResults = response.message.split(', ');
                currentPage = 1;
                displayResults();
            } else {
                displayErrorMessage('Resposta do servidor não contém mensagem.');
            }
        },
        error: function (xhr) {
            displayErrorMessage('Erro ao enviar palavra: ' + xhr.status + ': ' + xhr.statusText);
        }
    });
}

function displayResults() {
    const resultsContainer = document.getElementById('results');
    resultsContainer.innerHTML = '';
    const startIndex = (currentPage - 1) * itemsPerPage;
    const endIndex = Math.min(startIndex + itemsPerPage, searchResults.length);

    for (let i = startIndex; i < endIndex; i++) {
        const resultItem = document.createElement('li');
        resultItem.textContent = searchResults[i];
        resultsContainer.appendChild(resultItem);
    }

    document.getElementById('pagination').style.display = searchResults.length > itemsPerPage ? 'block' : 'none';
    document.getElementById('prevButton').disabled = currentPage === 1;
    document.getElementById('nextButton').disabled = endIndex >= searchResults.length;
}

function changePage(increment) {
    currentPage += increment;
    displayResults();
}

function displayErrorMessage(message) {
    const resultsContainer = document.getElementById('results');
    const resultItem = document.createElement('li');
    resultItem.textContent = message;
    resultsContainer.appendChild(resultItem);
}

function passar_link(event) {
    event.preventDefault();
    console.log("testa_ligação_links");

    $.ajax({
        url: 'http://localhost:8080/passar_link',
        type: 'POST',
        contentType: 'application/json',
        dataType: 'json',
        data: JSON.stringify($('#palavra').val().trim()),
        success: function (response) {
            if (response && response.message) {
                searchResults = response.message.split(', ');
                currentPage = 1;
                displayResults();
            } else {
                displayErrorMessage('Resposta do servidor não contém mensagem.');
            }
        },
        error: function (xhr) {
            displayErrorMessage('Erro ao enviar palavra: ' + xhr.status + ': ' + xhr.statusText);
        }
    });
}

function top10pesquisas(event) {
    event.preventDefault();
    console.log("testa_ligação_top10");

    $.ajax({
        url: 'http://localhost:8080/top10',
        type: 'GET',
        contentType: 'application/json',
        dataType: 'json',
        success: function (response) {
            if (response && response.message) {
               searchResults = response.message.split(', ');
               currentPage = 1;
               displayResults();
            } else {
                alert('Resposta do servidor não contém mensagem.');
            }
        },
        error: function (xhr) {
            alert('Erro ao enviar palavra: ' + xhr.status + ': ' + xhr.statusText);
        }
    });
}

function status(event) {
    event.preventDefault();
    console.log("testa_ligação_status");

    $.ajax({
        url: 'http://localhost:8080/admin',
        type: 'GET',
        contentType: 'application/json',
        dataType: 'json',
        success: function (response) {
            if (response && response.message) {
                // Split the message into an array
                var messageArray = response.message.split(', ');

                // Update the activeDownloaders and totalLinks elements
                document.getElementById("activeDownloaders").textContent = "Número de Downloaders Ativos: " + messageArray[0];
                document.getElementById("totalLinks").textContent = "Número de Links Existentes: " + messageArray[1];


                // Update the linkList element
                var linkList = document.getElementById("linkList");
                linkList.innerHTML = ""; // Clear existing content
                for (var i = 2; i < messageArray.length; i++) { // Start from index 1 to skip the active downloaders count
                    var link = document.createElement("a");
                    link.href = messageArray[i];
                    link.textContent = messageArray[i];
                    linkList.appendChild(link);
                    linkList.appendChild(document.createElement("br")); // Add line break after each link
                }

                // Display pagination if needed
                var pagination = document.getElementById("pagination");
                pagination.style.display = "block";

            } else {
                alert('Resposta do servidor não contém mensagem.');
            }
        },

    });
}


document.addEventListener('DOMContentLoaded', function() {
    document.getElementById('enviar_teste').addEventListener('click', (e) => {
        e.preventDefault();
        passar_url(e);
    });

    document.getElementById('enviar_palavra').addEventListener('click', (e) => {
        e.preventDefault();
        passar_palavra(e);
    });

    document.getElementById('enviar_pedir_link').addEventListener('click', (e) => {
        e.preventDefault();
        passar_link(e);
    });

    document.getElementById('carregar').addEventListener('click', (e) => {
        e.preventDefault();
        top10pesquisas(e);
    });
    document.getElementById('admin').addEventListener('click', (e) => {
            e.preventDefault();
            status(e);
        });


    document.getElementById('searchForm').addEventListener('submit', passar_palavra);
    document.getElementById('prevButton').addEventListener('click', () => changePage(-1));
    document.getElementById('nextButton').addEventListener('click', () => changePage(1));


});


