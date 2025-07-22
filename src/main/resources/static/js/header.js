document.addEventListener('DOMContentLoaded', function() {
    const searchForm = document.querySelector('.header-right form');
    const keywordInput = document.getElementById('header-search-form');


    if(searchForm && keywordInput){
        searchForm.addEventListener('submit', function(e) {
              const keyword = keywordInput.value.trim();
              if (keyword.length > 0 && keyword.length < 2) {
                alert('검색어는 두 글자 이상 입력해주세요.');
                e.preventDefault();
              }
        });
    }
});