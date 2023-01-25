const form = window.document.getElementById('writeForm');
const mainPhotoButton = form.querySelector('[rel="photoButton"]');
let areaCount = form.querySelector('.areaCount');

// 단어 중 숫자만 추출 해주는 함수
function regex(val){
    let number = /[^0-9]/g;
    return parseInt(val.replace(number,""));
}
// 빈문자열 확인
function isEmpty(str){
    return (str === '' || str === undefined || str === null || str === 'null');
}


//리스트 추가 함수
function listAdd() {
    let parent = event.target.closest('.group');
    let groupNum = regex(parent.id);
    let listCount = parent.querySelector('.list-count');
    let listIndex = parseInt(listCount.innerText);
    listIndex += 1;
    listCount.innerText = listIndex;
    const list = `
    <li id="liMaterial${groupNum}_${listIndex}" class="material-list">
        <label class="label">
            <input type="text" name="cookMaterialNM_${groupNum}_${listIndex}" rel="cookMaterialNM"
                   class="cook-material input">
            <input type="text" name="cookMaterialCT_${groupNum}_${listIndex}" rel="cookMaterialCT"
                   class="cook-material input">            
        </label>
        <i class="fa-solid fa-circle-xmark icon"></i>
    </li>
    `;

    const listElement = new DOMParser().parseFromString(list,'text/html').querySelector('.material-list');

    parent.querySelector('.materialArea').appendChild(listElement);
    form.querySelector('#materialArea' + groupNum).appendChild(listElement);
}

//그룹 삭제 함수
function removeGroup(){
    let removeValue = event.target.value;
    let groupValue = regex(event.target.closest('.group').id);
    if (groupValue !== 1){
        form.querySelector('#materialGroup'+removeValue).remove();
    } else {
        alert("삭제불가");
    }
}
//스탭 이미지 추가 함수
function stepImageUpload(){
    let stepIdNumber = regex(event.target.id);
    let stepImageButton = form.querySelector('#stepInput'+stepIdNumber);
    stepImageButton.click();
    // 스텝 이미지 프리뷰
    stepImageButton.onchange = e => {
        e.preventDefault();
        let stepBlob = new Blob(stepImageButton.files, {type: "image/*"});
        let stepSrc = URL.createObjectURL(stepBlob);
        form.querySelector('#stepImage'+stepIdNumber).setAttribute('src', stepSrc);

    }
}

// 대표사진 업로드 버튼 클릭
window.document.getElementById('mainPhoto').addEventListener('click', () =>{
    mainPhotoButton.click();
    // 메인 이미지 프리뷰
    mainPhotoButton.onchange = e => {
        e.preventDefault();
        const blob = new Blob(form['mainPhoto'].files, {type: "image/*"});
        const mainSrc = URL.createObjectURL(blob);
        form.querySelector('[rel="mainPhoto"]').setAttribute('src', mainSrc);
    };
});

// 리스트 추가 이벤트 +추가 버튼 클릭
form.querySelector('.add-list').addEventListener('click', listAdd);


// 재료 리스트 영역 추가 버튼 클릭
form.querySelector('[rel="ulAdd"]').addEventListener('click', () => {
    let areaIndex = parseInt(areaCount.innerText);

    areaIndex += 1;
    areaCount.innerText = areaIndex.toString();

    const materialContainer =`                
    <div id="materialGroup${areaIndex}" class="group">
       <span class="num list-count">2</span>       
        <div class="material-box">                 
           <div class="material-title">
               <input type="text" name="materialTitle" rel="materialTitle" required
                          class="material-title-input" value="[재료]">
               <div class="button-container">
                   <button class="material-button" type="button">
                       한번에 넣기
                   </button>
                   <button class="material-button removeGroup" name="removeArea" type="button" value="${areaIndex}">
                       묶음 삭제
                   </button>
               </div>
           </div>
           <ul id="materialArea${areaIndex}" class="materialArea">            
               <li id="liMaterial${areaIndex}_1" class="material-list">
                   <label class="label">
                       <input type="text" name="cookMaterialNM" rel="cookMaterialNM"
                              class="cook-material input">
                       <input type="text" name="cookMaterialCT" rel="cookMaterialCT"
                              class="cook-material input">
                   </label>
                   <i class="fa-solid fa-circle-xmark icon"></i>
               </li>
               <li id="liMaterial${areaIndex}_2" class="material-list">
                   <label class="label">
                       <input type="text" name="cookMaterialNM" rel="cookMaterialNM"
                              class="cook-material input">
                       <input type="text" name="cookMaterialCT" rel="cookMaterialCT"
                              class="cook-material input">
                   </label>
                   <i class="fa-solid fa-circle-xmark icon"></i>
               </li>
           </ul>
          </div>
       <div class="add-list">
        <button type="button" class="list-add">
            <i class="fa-solid fa-circle-plus icon"></i>추가
        </button>
       </div>
    </div>`;



    const materialElement = new DOMParser().parseFromString(materialContainer, 'text/html')
        .querySelector('.group');

    materialElement.querySelector('.removeGroup').addEventListener('click', removeGroup);
    materialElement.querySelector('.add-list').addEventListener('click',listAdd);

    form.querySelector('.material').appendChild(materialElement);
});

// 묶음 삭제 버튼 클릭
form['removeArea'].addEventListener('click', removeGroup);


// 스텝 이미지 업로드
form.querySelector('.stepImg').addEventListener('click', stepImageUpload);

// 스탭 추가
form.querySelector('[rel="stepAdd"]').addEventListener('click', () => {
    let stepNum = form.querySelector('.step-num');
    let stepIndex = parseInt(stepNum.innerText);
    stepIndex += 1;
    stepNum.innerText = stepIndex.toString();
    const stepBox = `
    <div id="stepItem${stepIndex}" class="step-box">        
        <p class="step-title">
            Step${stepIndex}
        </p>
        <textarea name="stepText" id="stepText${stepIndex}" class="step-content" placeholder="예) 고기가 반쯤 익어갈때 양파를 함께 볶아요"></textarea>
        <img id="stepImage${stepIndex}" src="https://recipe1.ezmember.co.kr/img/pic_none2.gif" class="image stepImg" alt="업로드이미지">
        <input id="stepInput${stepIndex}" type="file" accept="image/*" name="stepImage" class="step-input input">
        <div class="step-button">
            <a href="#"></a>
            <a href="#"></a>
            <a href="#"></a>
            <a href="#"></a>
            <a href="#"></a>
        </div>
    </div>
    `;
    const stepElement = new DOMParser().parseFromString(stepBox, 'text/html').querySelector('.step-box');
    stepElement.querySelector('.stepImg').addEventListener('click',stepImageUpload);

    form.querySelector('[rel="stepAdd"]').before(stepElement);
});

// 완성 이미지 업로드
const completeImages = form.querySelectorAll('.upload-image');
for (const img of completeImages){
    // 완성 이미지 클릭시 이벤트
    img.addEventListener('click', () => {
        let completePhotoButton = event.target.parentElement.querySelector('.complete');
        completePhotoButton.click();
        // 완성사진 프리뷰
        completePhotoButton.onchange = e => {
            e.preventDefault();
            let completePhoto = new Blob(completePhotoButton.files, {type: "image/*"});
            let completeSrc = URL.createObjectURL(completePhoto);
            img.querySelector('.image').setAttribute('src',completeSrc);
        };
    });
}

//버튼 아이디 값 가져오기
let buttonId;
for (let button of form['submit']){
    button.addEventListener('click', () => {
        buttonId = event.target.id;
    })
}

// 저장 후 공개 버튼클릭 글 업로드
form.onsubmit = e => {
    e.preventDefault();
    const formData = new FormData();

    // 메인 사진
    let mainPhotoFile = form['mainPhoto'].files;
    if (mainPhotoFile.length === 0){
        alert("메인사진을 등록해주세요.");
        form.querySelector('[rel="mainPhoto"]').focus();
        return false;
    } else {
        for(let mainPhoto of mainPhotoFile) {
            formData.append('mainPhoto', mainPhoto);
        }
    }


    // 재료 항목 배열
    let materialGroupArray = form.querySelectorAll('.group');
    let materialJsonArray = [];
    for (let group of materialGroupArray){
        let materialNameArray = Array.from(group.querySelectorAll('[rel="cookMaterialNM"]'));
        let materialCountArray = Array.from(group.querySelectorAll('[rel="cookMaterialCT"]'));
        let materialJson = {
            materialTitle: group.querySelector('[rel="materialTitle"]').value,
            materialName: materialNameArray.map( (name) => {
                return name.value;
            }),
            materialCount: materialCountArray.map( (count) => {
                return count.value;
            })
        };

        materialJsonArray.push(materialJson);
    }
    let materialString = JSON.stringify(materialJsonArray);

    // 스텝 이미지
    let stepImageArray = form['stepImage'];
    for (let stepImageObject of stepImageArray) {
        if (stepImageObject.value === ''){
            formData.append("stepImage","empty");
        } else {
            for (let stepPhoto of stepImageObject.files){
                formData.append("stepPhoto",stepPhoto);
            }
        }
    }

    // 스탭 텍스트
    let stepContent = form['stepText'];
    if (stepContent.length > 0){
        for (let step of stepContent) {
            formData.append(`stepContent`, step.value);
        }
    }

    //select 값
    let selectTag = form.querySelectorAll('.select');
    for (let select of selectTag){
        if (parseInt(select.value) !== 0){
            formData.append(select.id,select.value);
        } else {
            alert("카테고리를 선택해주세요");
            select.focus();
            return false;
        }
    }


    // 제목, 내용, 팁
    let articleText = form.querySelectorAll('.textInput');
    for (let input of articleText){
        if (!isEmpty(input.value)){
            formData.append(input.id,input.value);
        } else {
            alert(input.getAttribute('rel')+"입력해주세요");
            input.focus();
            return false;
        }
    }
    // 완료 이미지
    let completePhotoArray = form['completePhoto'];
    for (let completePhotoObject of completePhotoArray) {
        let i = 0;
        if (completePhotoObject.value === ''){
            i++;
        } else {
            for (let completePhoto of completePhotoObject.files) {
                formData.append('completePhoto', completePhoto);
            }
        }
        if (i === 4){
            alert("완성사진을 1개 이상 올려주세요.");
            completePhotoArray[0].focus();
            return false;
        }

    }


    const xhr = new XMLHttpRequest();
    formData.append('materialJson', materialString);
    formData.append('isOpen', buttonId);
    xhr.open('POST', './write');
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject['result']) {
                    case 'success':
                        alert("글쓰기 성공");
                        break;
                    default:

                }
            } else {
                alert("알 수 없는 오류");
            }
        }
    };
    xhr.send(formData);
};