const previewImage = document.querySelector('.previewImage');
const fileInput = document.getElementById('fileInput');


if(previewImage && fileInput){
    previewImage.addEventListener('click', () => { fileInput.click(); });
    fileInput.addEventListener('change', async () => {
        const files = fileInput.files;
        if(files.length > 0) {
            const reader = new FileReader();
            reader.onload = (e) => {
                previewImage.src = e.target.result; };
            reader.readAsDataURL(files[0]);

            const formData = new FormData();
            for(let i = 0; i < files.length; i++) {
                const file = files[i];
                formData.append('files', file);
            }
            const response = await fetch('/posts', {
                method: 'POST',
                body: formData
            });
            if (response.ok) {
                const urls = await response.json();
                console.log(urls); // S3에 올라간 URL 리스트!
                alert('업로드 성공!');
            } else {
                alert('업로드 실패!');
            }
        }
    });
}