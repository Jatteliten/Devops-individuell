let currentPage = /*[[${page}]]*/ 0;

function loadMore() {
    const loadMoreButton = document.getElementById("load-more");
    loadMoreButton.style.display = 'none';
    const loadingButton = document.getElementById("loading-button");
    loadingButton.style.display = 'inline-block';

    currentPage++;
    fetch(`/pokemon/list?page=${currentPage}`)
        .then(response => response.text())
        .then(data => {
            const parser = new DOMParser();
            const doc = parser.parseFromString(data, 'text/html');
            const newRows = doc.querySelectorAll('#pokemon-list-body tr');
            const pokemonListBody = document.getElementById('pokemon-list-body');

            newRows.forEach(row => pokemonListBody.appendChild(row));

            if (newRows.length < 50) {
                loadMoreButton.style.display = 'none';
            } else {
                loadMoreButton.style.display = 'inline-block';
            }
            loadingButton.style.display = 'none';
        })
        .catch(error => {
            console.error('Error loading more Pokémon:', error);
            loadMoreButton.style.display = 'inline-block';
        });
}