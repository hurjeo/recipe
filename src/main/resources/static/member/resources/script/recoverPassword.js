const form = window.document.getElementById('form');

const Warning = {
    getElement: () => form.querySelector('[rel="warningRow"]'),
    show: (text) => {
        const warningRow = Warning.getElement();
        warningRow.querySelector('.text').innerText = text;
        warningRow.classList.add('visible');
    },
    hide: () => Warning.getElement().classList.remove('visible')
};

let emailAuthIndex = null;
setInterval(() => {
    if (emailAuthIndex === null) {
        return;
    }

    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('index', emailAuthIndex);
    xhr.open('POST', './recoverPasswordEmail');
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                console.log(xhr.responseText);
                switch (responseObject['result']) {
                    case 'success':
                        form['code'].value = responseObject['code'];
                        form['salt'].value = responseObject['salt'];
                        form.querySelector('[rel="messageRow"]').classList.remove('visible');
                        form.querySelector('[rel="passwordRow"]').classList.add('visible');
                        emailAuthIndex = null;
                        break;
                    default:
                }
            }
        }
    }
    xhr.send(formData);
}, 1000);

form['emailSend'].addEventListener('click', () => {
    Warning.hide();
    if (form['email'].value === '') {
        Warning.show('이메일을 입력해 주세요.');
        form['email'].focus();
        return;
    }
    Cover.show('계정을 확인하고 있습니다. \n 잠시만 기다려 주세요.');
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('email', form['email'].value);
    formData.append('password', form['password'].value);
    xhr.open('POST', './recoverPassword');
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            Cover.hide();
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject['result']) {
                    case 'success':
                        emailAuthIndex = responseObject['index'];
                        form['email'].setAttribute('disabled', 'disabled');
                        form['emailSend'].setAttribute('disabled', 'disabled');
                        form.querySelector('[rel="messageRow"]').classList.add('visible');
                        form['password'].focus();
                        form['password'].select();
                        break;
                    default:
                        Warning.show('해당 이메일을 찾을 수 없습니다.');
                }
            } else {
                Warning.show('서버와 통신할 수 없습니다.');
            }
        }
    }
    xhr.send(formData);
});

form['recover'].addEventListener('click', () => {
    Warning.hide();
    if (form['password'].value === '') {
        Warning.show('새로운 비밀번호를 입력해 주세요.');
        form['password'].focus();
        return;
    }
    if (form['password'].value !== form['passwordCheck'].value) {
        Warning.show('비밀번호가 서로 일치하지 않습니다.');
        form['passwordCheck'].focus();
        form['passwordCheck'].select();
        return;
    }

    Cover.show('비밀번호를 재설정하고 있습니다.. \n 잠시만 기다려 주세요.');
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('email', form['email'].value);
    formData.append('code', form['code'].value);
    formData.append('salt', form['salt'].value);
    formData.append('password', form['password'].value);
    xhr.open('PATCH', './recoverPassword');
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            Cover.hide();
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject['result']) {
                    case 'success':
                        alert('비밀번호를 성공적으로 재설정 하였습니다. \n\n 다시 로그인 해주시기 바랍니다.');
                        window.location.href = 'login';
                        break;
                    default:
                        Warning.show('비밀번호를 재설정하지 못하였습니다. 세션이 만료되었을 수도있으니 다시 인증해 주시기 바랍니다.');
                }
            } else {
                Warning.show('서버와 통신하지 못하였습니다.');
            }
        }
    }
    xhr.send(formData);
});

