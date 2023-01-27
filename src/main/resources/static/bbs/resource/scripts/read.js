const commentForm = window.document.getElementById('commentForm');

commentForm.querySelector('.image').addEventListener('click', () =>{
    commentForm['commentInput'].click();
    commentForm['commentInput'].onchange = e => {
        e.preventDefault();
        const blob = new Blob(commentForm['commentForm'].files, {type: "image/*"});
        const commentImage = URL.createObjectURL(blob);
        commentForm.querySelector('.image').setAttribute('src',commentImage);
    }
})

const loadComments = () => {


}

loadComments();