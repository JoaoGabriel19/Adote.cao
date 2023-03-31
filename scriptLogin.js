function login(){
    email = document.getElementById("login").value;
    password = document.getElementById("password").value;

    let httpRequest = new XMLHttpRequest();
    const URL_SERVLET_LOGIN = "http://localhost:8080/adoteCaoProjeto/LoginServlet";
    httpRequest.onreadystatechange=function(){
        if(httpRequest.readyState === XMLHttpRequest.DONE){
            if(httpRequest.status === 200){
                console.log("Request realizado");
                let sessionData = JSON.parse(httpRequest.responseText);
                window.sessionStorage.setItem("isOng", sessionData.isOng);
                window.sessionStorage.setItem("isLogged", sessionData.isLogged);
                window.sessionStorage.setItem("jwt", sessionData.jwt);
                console.log(sessionData);

                if(sessionData.isOng){
                    window.location.href = "http://127.0.0.1:5500/ongMenu.html";
                }else{
                    window.location.href = "http://127.0.0.1:5500/adotanteMenu.html";
                }
            }
        }
    }
    httpRequest.open("POST", URL_SERVLET_LOGIN, true);
    httpRequest.setRequestHeader('Content-type','application/x-www-form-urlencoded');
    httpRequest.send("login="+email+"&password="+password);
}

function getSession(){
    let
}