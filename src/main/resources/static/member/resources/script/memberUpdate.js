const containerEtc = window.document.getElementById('container_etc');

const loadComments = () => {
    containerEtc.innerText = '';
    const url = new URL(window.location.href);
    // const searchParams = url.searchParams;
    const xhr = new XMLHttpRequest();

    xhr.open('GET', './memberUpdate');
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseArray = JSON.parse(xhr.responseText);
                    }
        }
    }
}