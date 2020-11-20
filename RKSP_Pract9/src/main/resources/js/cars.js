var d = document;
var globalRow;

document.addEventListener("DOMContentLoaded", function() {
    console.log("here");
    let addButton = d.getElementById('add');
    addButton.onclick = addRow;

    let type = 'downloadTable';
    type = 'type=' + encodeURIComponent(type);
    let request = new XMLHttpRequest();
    request.open('POST', 'http://localhost:8080/carsTable.html', true);
    
    request.addEventListener('readystatechange', function(){
        if((request.readyState==4)&&(request.status==200)){
            console.log(request);
            console.log(request.responseText);
            console.log(request.status);
            let tbody = d.getElementById('dynamic');
            tbody.innerHTML = request.responseText;
        }
    });
    request.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    request.send(type);
});
function addRow(){
    console.log("here1");
    if((d.getElementById('price').value !== '')&&(d.getElementById('color').value !== '')&&(d.getElementById('year').value !== '')){
        let addButton = d.getElementById('add');
        if(addButton.value === 'Обновить'){
            console.log(addButton.value);
            updateRow(addButton);
            return;
        }
        let request = new XMLHttpRequest();
        request.open('POST', 'http://localhost:8080/carsTable.html', true);
        request.addEventListener('readystatechange', function(){
            if((request.readyState==4)&&(request.status==200)){
                console.log(request);
                console.log(request.responseText);

                let tbody = d.getElementById('dynamic');
                let id = request.responseText;

                let row = d.createElement('TR');
                tbody.appendChild(row);
                row.setAttribute('id', id);

                let td1 = d.createElement('TD');
                let td2 = d.createElement('TD');
                let td3 = d.createElement('TD');
                let btnDel = d.createElement('input');
                let btnChange = d.createElement('input');

                btnDel.setAttribute('value', 'удалить');
                btnDel.setAttribute('type', 'button');
                btnDel.addEventListener('click', function(){
                    let thisButton = this;
                    deleteRow(thisButton.parentElement);
                });

                btnChange.setAttribute('value', 'изменить');
                btnChange.setAttribute('type', 'button');
                btnChange.addEventListener('click', function(){
                    let thisButton = this;
                    selectRow(thisButton.parentElement);
                });

                row.appendChild(td1);
                row.appendChild(td2);
                row.appendChild(td3);
                row.appendChild(btnChange);
                row.appendChild(btnDel);

                td1.innerHTML = d.getElementById('price').value;
                td2.innerHTML = d.getElementById('color').value;
                td3.innerHTML = d.getElementById('year').value;

                d.getElementById('price').value = '';
                d.getElementById('color').value = '';
                d.getElementById('year').value = '';

            }
        });
        let price = d.getElementById('price').value;
        let color = d.getElementById('color').value;
        let year = d.getElementById('year').value;
        let type = 'add';
        price = 'price=' + encodeURIComponent(price);
        color = ' color=' + encodeURIComponent(color);
        year = ' year=' + encodeURIComponent(year);
        type = ' type=' + encodeURIComponent(type);

        request.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
        request.send(price + color + year + type);

        console.log("here2");
    }
};
function updateRow(addButton){
    console.log('In updateRow: ' + addButton.value);
    
    let request = new XMLHttpRequest();
    request.open('POST', 'http://localhost:8080/carsTable.html', true);
    request.addEventListener('readystatechange', function(){
        if((request.readyState==4)&&(request.status==200)){
            console.log(request);
            console.log(request.responseText);

            let result = request.responseText;
            if(result === 'Updated'){
                globalRow.style.backgroundColor = 'white';

                globalRow.cells[0].innerText = d.getElementById('price').value;
                globalRow.cells[1].innerText = d.getElementById('color').value;
                globalRow.cells[2].innerText = d.getElementById('year').value;

                addButton.value = 'Добавить';
                d.getElementById('price').value = '';
                d.getElementById('color').value = '';
                d.getElementById('year').value = '';
            }
        }
    });
    let id = globalRow.getAttribute('id');
    let type = 'update';
    let price = d.getElementById('price').value;
    let color = d.getElementById('color').value;
    let year = d.getElementById('year').value;    
    id = 'id=' + encodeURIComponent(id);
    type = ' type=' + encodeURIComponent(type);
    price = ' price=' + encodeURIComponent(price);
    color = ' color=' + encodeURIComponent(color);
    year = ' year=' + encodeURIComponent(year);

    request.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    request.send(id + type + price + color + year);
};
window.deleteRow = function (row){
    console.log("In deleteRow: " + row);
    let id = row.getAttribute("id");
    let type = 'delete';
    id = 'id=' + encodeURIComponent(id);
    type = ' type=' + encodeURIComponent(type);
    
    let request = new XMLHttpRequest();
    request.open('POST', 'http://localhost:8080/carsTable.html', true);
    request.addEventListener('readystatechange', function(){
        if((request.readyState==4)&&(request.status==200)){
            console.log(request);
            console.log(request.responseText);

            let result = request.responseText;
            if(result === 'Deleted'){
                row.parentElement.removeChild(row);
            }
        }
    });
    request.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    request.send(id + type);
};
window.selectRow = function (row){
    console.log("In selectRow: " + row);
    if(globalRow){
        globalRow.style.backgroundColor = 'white';
    }
    if((row.cells[0].innerText == d.getElementById('price').value) &&
    (row.cells[1].innerText == d.getElementById('color').value) &&
    (row.cells[2].innerText == d.getElementById('year').value)){
            let addButton = d.getElementById('add');
            addButton.value = 'Добавить';
            d.getElementById('price').value = '';
            d.getElementById('color').value = '';
            d.getElementById('year').value = '';
            globalRow = null;
    } else {
        row.style.backgroundColor = "yellow";

        let price = row.cells[0].innerText;
        let color = row.cells[1].innerText;
        let year = row.cells[2].innerText;

        d.getElementById("price").value = price;
        d.getElementById("color").value = color;
        d.getElementById("year").value = year;

        let addButton = d.getElementById("add");
        addButton.value = "Обновить";
        
        globalRow = row;
    }
};