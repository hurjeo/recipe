const form = window.document.getElementById('form');
const kakao = window.document.getElementById('kakao');
const naver = window.document.getElementById('naver');
const google = window.document.getElementById('google');
const facebook = window.document.getElementById('facebook');

const Submit = {
    show: (text) => {
        form.querySelector('[rel="submitText"]').innerText = text;
        form.querySelector('[rel="submitRow"]').classList.add('visible');
    },
    hide: () => {
        form.querySelector('[rel="submitRow"]').classList.remove('visible');
    }
};

form.onsubmit = e => {
    e.preventDefault();
    Submit.hide();
    if (form['email'].value === '') {
        Submit.show('이메일을 입력해 주세요');
        form['email'].focus();
        return;
    }
    if (form['password'].value === '') {
        Submit.show('비밀번호를 입력해 주세요.');
        form['password'].focus();
        form['password'].select();
        return;
    }
    Cover.show('회원정보를 확인하고 있습니다. \n 잠시만 기다려주세요.');
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('email', form['email'].value);
    formData.append('password', form['password'].value);
    xhr.open('POST', './login');
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            Cover.hide();
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject['result']) {
                    case 'success':
                        // window.location.replace('http://localhost:8080/bbs/write');
                        alert("로그인 성공");
                        break;
                    default:
                        Submit.show('비밀번호가 일치하지 않습니다.');
                }
            } else {
                Submit.show('서버와 연결하지 못하였습니다.');
            }
        }
    }
    xhr.send(formData);
};

kakao?.addEventListener('click', e => {
    e.preventDefault();
    window.open(
        'https://kauth.kakao.com/oauth/authorize?client_id=5ed9066f02e45714ed6822817956260d&redirect_uri=https://ofallrecipe.com/member/kakao&response_type=code','_blank', 'width=480', 'height=519');
});

naver?.addEventListener('click', e => {
    e.preventDefault();
    window.open(
        'https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id=0sxOPxjHRB5ik5dfST85&state=STATE_STRING&redirect_uri=https://ofallrecipe.com/member/naver','_blank');
});

google?.addEventListener('click', e => {
    e.preventDefault();
    window.open('https://accounts.google.com/o/oauth2/v2/auth?scope=https%3A//www.googleapis.com/auth/drive.metadata.readonly&include_granted_scopes=true&response_type=code&state=state_parameter_passthrough_value&redirect_uri=https://ofallrecipe.com/member/google&client_id=608196039616-p7on7p7379h5j4vatkh0uttgg14nrr4u.apps.googleusercontent.com', '_blank', 'width=480', 'heigth=519');
});

facebook.addEventListener('click', e => {
    e.preventDefault();
    alert('준비중입니다.')
});