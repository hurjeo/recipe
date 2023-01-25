const form = window.document.getElementById('form');

const Submit = {
    getElement: () => form.querySelector('[rel="warningRow"]'),
    show: (text) => {
        const submit = Submit.getElement();
        submit.querySelector('.text').innerText = text;
        submit.classList.add('visible');
    },
    hide: () => Submit.getElement().classList.remove('visible')
};

const Email = {
    getElement: () => form.querySelector('[rel="emailRow"]'),
    show: (text) => {
        const email = Email.getElement();
        email.querySelector('.text').innerText = text;
        email.classList.add('visible');
    },
    hide: () => Email.getElement().classList.remove('visible')
};

form.onsubmit = e => {
    e.preventDefault();
    Submit.hide();
    if (form['name'].value === '') {
        Submit.show('이름을 입력해 주세요.');
        form['name'].focus();
        return;
    }
    if(form['age'].value === ''){
        Submit.show('나이를 입력해 주세요.');
        form['age'].focus();
        return;
    }
    if (form['contact'].value === '') {
        Submit.show('연락처를 입력해 주세요.');
        form['contact'].focus();
        return;
    }

    Cover.show('회원 정보를 확인하고 있습니다. \n 잠시만 기다려 주세요.');
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('name', form['name'].value);
    formData.append('age', form['age'].value)
    formData.append('contact', form['contact'].value);
    xhr.open('POST', './recoverEmail');
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            Cover.hide();
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject['result']) {
                    case 'success':
                        form['recoveryEmail'].setAttribute('disabled', 'disabled');
                        Email.show('찾으시는 회원님의 이메일은 \n' + responseObject['email'] + ' \n 입니다.');
                        form.querySelector('[rel="loginsubmit"]').classList.add('visible');
                        break;
                    default:
                        Submit.show('일치하는 정보가 없습니다.');
                }
            } else {
                Submit.show('서버와 통신하지 못하였습니다. 잠시 후 다시 시도해 주세요.');
            }
        }
    }
    xhr.send(formData);
};