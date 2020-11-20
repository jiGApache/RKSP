document.addEventListener("DOMContentLoaded", function(){
    let button = document.getElementById("send");
    button.addEventListener("click", function(){
        let roman = document.getElementById("roman").value;
        roman = 'romanNumber=' + encodeURIComponent(roman);
        let request = new XMLHttpRequest();
        request.open('POST', 'http://localhost:8080/calculate.html', true);
        request.addEventListener('readystatechange', function(){
            if((request.readyState==4)&&(request.status==200)){
            console.log(request);
            console.log(request.responseText);
            let result = document.getElementById("result");
            result.innerHTML = request.responseText;
            }
        });
        request.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
        request.send(roman);
    });
});