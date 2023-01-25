const form = window.document.getElementById('form');

const Warning = {
    show: (text) => {
        form.querySelector('[rel="warningText"]').innerText = text;
        form.querySelector('[rel="warning"]').classList.add('visible');
    },
    hide: () => {
        form.querySelector('[rel="warning"]').classList.add('visible');
    }
};
const EmailWarning = {
    show: (text) => {
        const emailWarning = form.querySelector('[rel="emailWarning"]');
        emailWarning.innerText = text;
        emailWarning.classList.add('visible');
    },
    hide: () => {
        form.querySelector('[rel="emailWarning"]').classList.remove('visible');
    }
}

form.querySelector('[rel="nextButton"]').addEventListener('click', () => {
    form.querySelector('[rel="warning"]').classList.remove('visible');
    if (form.classList.contains('step1')) { // step1 이 포함하고 있을때만 발생
        console.log('스탭1처리')
        if (!form['termAgree'].checked) {
            form.querySelector('[rel="warningText"]').innerText = '서비스 이용 약관 및 개인정보 처리방침을 읽고 동의해 주세요.';
            form.querySelector('[rel="warning"]').classList.add('visible');
            return;
        }
        form.querySelector('[rel="stepText"]').innerText = '개인정보 입력';
        form.classList.remove('step1');
        form.classList.add('step2');
    } else if (form.classList.contains('step2')) {
        if (!form['emailSend'].disabled || !form['emailVerify'].disabled) {
            Warning.show('이메일 인증을 완료해 주세요.');
            (form['email'] || form['emailAuthCode'])?.focus();
            return;
        }
        if (form['password'].value === '') {
            Warning.show('비밀번호를 입력해 주세요.');
            form['password'].focus();
            return;
        }
        if (form['password'].value !== form['passwordCheck'].value) {
            Warning.show('비밀번호가 서로 일치하지 않습니다.');
            form['passwordCheck'].focus();
            return;
        }

        if (form['age'].value === '') {
            Warning.show('나이를 입력해 주세요.');
            form['age'].focus();
            return;
        }

        if(form['gender'].value === ''){
            Warning.show('성별을 클릭해 주세요.');
            return;
        }

        if (form['nickname'].value === '') {
            Warning.show('닉네임을 입력해 주세요.');
            form['nickname'].focus();
            return;
        }
        if (form['name'].value === '') {
            Warning.show('이름을 입력해 주세요.');
            form['name'].focus();
            return;
        }
        if (form['contact'].value === '') {
            Warning.show('연락처를 입력해 주세요.');
            form['contact'].focus();
            return;
        }
        if (form['addressPostal'].value === '') {
            Warning.show('주소 찾기를 통해 주소를 입력해 주세요.');
            form['addressPostal'].focus();
            return;
        }
        if (form['addressSecondary'].value === '') {
            Warning.show('상세 주소를 입력해 주세요.');
            form['addressSecondary'].focus();
            return;
        }
        Cover.show('회원가입 진행 중입니다. \n\n 잠시만 기다려주세요.');

        const xhr = new XMLHttpRequest();
        const formData = new FormData();
        formData.append('email', form['email'].value);
        formData.append('code', form['emailAuthCode'].value);
        formData.append('salt', form['emailAuthSalt'].value);
        formData.append('password', form['password'].value);
        formData.append('imageData', form['images'].value);
        formData.append('age', form['age'].value);
        formData.append('gender', form['gender'].value);
        formData.append('nickname', form['nickname'].value);
        formData.append('name', form['name'].value);
        formData.append('contact', form['contact'].value);
        formData.append('addressPostal', form['addressPostal'].value);
        formData.append('addressPrimary', form['addressPrimary'].value);
        formData.append('addressSecondary', form['addressSecondary'].value);

        xhr.open('POST', './register');
        xhr.onreadystatechange = () => {
            if (xhr.readyState === XMLHttpRequest.DONE) {
                Cover.hide();
                if (xhr.status >= 200 && xhr.status < 300) {
                    const responseObject = JSON.parse(xhr.responseText);
                    switch (responseObject['result']) {
                        case 'email_not_verified':
                            Warning.show('이메일 인증이 완료되지 않았습니다.');
                            break;
                        case 'success':
                            form.querySelector('[rel="stepText"]').innerText = '회원가입 완료';
                            form.querySelector('[rel="nextButton"]').innerText = '로그인하러 가기';
                            form.classList.remove('step2');
                            form.classList.add('step3');
                            break;
                        default:
                            Warning.show('알 수 없는 이유로 회원가입에 실패하였습니다. 잠시 후 다시 시도해 주세요.')
                    }
                } else {
                    Warning.show('서버와 통신하지 못하였습니다. 잠시 후 다시 시다해 주세요.')
                }
            }
        }
        xhr.send(formData);
    } else if (form.classList.contains('step3')) {
        window.location.href = 'login';
    }
});

form['emailSend'].addEventListener('click', () => {
    form.querySelector('[rel="emailWarning"]').classList.remove('visible');
    if (form['email'].value === '') {
        EmailWarning.show('이메일 주소를 입력해 주세요.');
        form['email'].focus();
        return;
    }
    if (!new RegExp('^(?=.{9,50}$)([\\da-zA-Z\\-_.]{4,})@([\\da-z\\-]{2,}\\.)?([\\da-z\\-]{2,})\\.([a-z]{2,15})(\\.[a-z]{2})?$').test(form['email'].value)) {
        EmailWarning.show('올바른 이메일 주소를 입력해 주세요.');
        form['email'].focus();
        return;
    }

    Cover.show('인증번호를 전송하고있습니다. \n잠시만 기다려 주세요.');
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('email', form['email'].value);

    xhr.open('POST', './email');
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            Cover.hide();
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject['result']) {
                    case 'success':
                        EmailWarning.show('인증 번호를 전송하였습니다. 전송된 인증번호는 5분간만 유효합니다.');
                        form['email'].focus();
                        form['email'].setAttribute('disabled', 'disabled');
                        form['emailSend'].setAttribute('disabled', 'disabled');
                        form['emailAuthCode'].removeAttribute('disabled');
                        form['emailAuthCode'].focus();
                        form['emailAuthSalt'].value = responseObject['salt'];
                        form['emailVerify'].removeAttribute('disabled');
                        break;
                    case 'email_duplicated':
                        EmailWarning.show('해당 이메일은 이미 사용 중니다.');
                        form['email'].focus();
                        form['email'].focus();
                        form['email'].select();
                        break;
                    default:
                        EmailWarning.show('알 수 없는 이유로 인증 번호를 전송하지 못 하였습니다. 잠시 후 다시 시도해 주세요.');
                        form['email'].focus();
                        form['email'].focus();
                        form['email'].select();
                        break;
                }
            } else {
                EmailWarning.show('서버와 통신하지 못하였습니다. 잠시 후 다시 시도해 주세요.');
                form['email'].focus();
            }
        }
    }
    xhr.send(formData);
});

form['emailVerify'].addEventListener('click', () => {
    if (form['emailAuthCode'].value === '') {
        EmailWarning.show('인증 번호를 입력해 주세요.');
        form['emailAuthCode'].focus();
        return;
    }
    if (!new RegExp('^(\\d{6})$').test(form['emailAuthCode'].value)) {
        EmailWarning.show('올바른 인증 번호를 입력해 주세요.');
        form['emailAuthCode'].focus();
        form['emailAuthCode'].select();
        return;
    }
    Cover.show('인증 번호를 확인하고 있습니다. \n\n 잠시만 기다려 주세요.');
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('email', form['email'].value);
    formData.append('code', form['emailAuthCode'].value);
    formData.append('salt', form['emailAuthSalt'].value);
    xhr.open('PATCH', 'email');
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            Cover.hide();
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject['result']) {
                    case 'expired':
                        EmailWarning.show('인증 정보가 만료되었습니다. 다시 시도해 주세요.');
                        form['email'].removeAttribute('disabled');
                        form['email'].focus();
                        form['email'].select();
                        form['emailSend'].removeAttribute('disabled');
                        form['emailAuthCode'].value = '';
                        form['emailAuthCode'].setAttribute('disabled', 'disabled');
                        form['emailAuthSalt'].value = '';
                        form['emailVerify'].setAttribute('disabled', 'disabled');
                        break;
                    case 'success':
                        EmailWarning.show('이메일이 정상적으로 인증되었습니다.');
                        form['emailAuthCode'].setAttribute('disabled', 'disabled');
                        form['emailVerify'].setAttribute('disabled', 'disabled');
                        form['password'].focus();
                        break;
                    default:
                        EmailWarning.show('인증 번호가 올바르지 않습니다.');
                        form['emailAuthCode'].focus();
                        form['emailAuthCode'].select();
                }
            } else {
                EmailWarning.show('서버와 통신하지 못하였습니다. 잠시 후 다시 시도해 주세요.');
            }
        }
    };
    xhr.send(formData);
});

form['addressFind'].addEventListener('click', () => {
    new daum.Postcode({
        oncomplete: e => {
            form.querySelector('[rel="addressFindPanel"]').classList.remove('visible');
            form['addressPostal'].value = e['zonecode'];
            form['addressPrimary'].value = e['address'];
            form['addressSecondary'].value = '';
            form['addressSecondary'].focus();
        }
    }).embed(form.querySelector('[rel="addressFindPanelDialog"]'));
    form.querySelector('[rel="addressFindPanel"]').classList.add('visible');
});

form.querySelector('[rel="addressFindPanel"]').addEventListener('click', () => {
    form.querySelector('[rel="addressFindPanel"]').classList.remove('visible')
});

if (form) {
    form.querySelector('[rel="imageSelectButton"]').addEventListener('click', e => {
        e.preventDefault();
        form['images'].click();
    });
    form['images'].addEventListener('input', () => {
        const imageContainer = form.querySelector('[rel="imageContainer"]');
        imageContainer.querySelectorAll('img.image').forEach(x => x.remove());
        if (form['images'].files.length > 0) {
            form.querySelector('[rel="basicImage"]').classList.add('hidden');
        } else {
            form.querySelector('[rel="basicImage"]').classList.remove('hidden');
        }
        for (let file of form['images'].files) {
            const imageSrc = URL.createObjectURL(file);
            const imgElement = document.createElement('img');
            imgElement.classList.add('image');
            imgElement.setAttribute('src', imageSrc);
            imageContainer.append(imgElement);
        }
    })
}

