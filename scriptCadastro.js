let isOng = document.getElementById("isOng");
            isOng.addEventListener("change", showOngOptions);
            function showOngOptions() {
                let formContainer = document.getElementById("formContainer");

                // EXIBICAO DE OPCOES DE ONG
                if (isOng.checked) {
                    console.log("MARCADO"); 
                    // NOME DA ONG
                    let ongName = document.createElement("input");
                    ongName.setAttribute("id","ongName");
                    let labelOngName = document.createElement("label");
                    labelOngName.setAttribute("for", "ongName");
                    labelOngName.setAttribute("id","labelOngNameId")
                    labelOngName.innerText = "Nome da Ong"
                    formContainer.appendChild(labelOngName);
                    formContainer.appendChild(ongName);

                } else {
                    let ongName = document.getElementById("ongName");
                    let labelOngName = document.getElementById("labelOngNameId");
                    ongName.remove();
                    labelOngName.remove();
                }
            }

            function createAccount(event) {

                let httpRequest = new XMLHttpRequest();
                const URL_SERVLET_REGISTER = "http://localhost:8080/adoteCaoProjeto/RegisterServlet";
                httpRequest.onreadystatechange=function(){
                  if(httpRequest.readyState === XMLHttpRequest.DONE){
                    if(httpRequest.status === 200){
                      window.alert("Conta Criada com sucesso!");
                      window.location.href = "http://127.0.0.1:5500/login.html";
                    }else if(httpRequest.status === 400 || httpRequest.status === 422 || httpRequest.status === 409 || httpRequest.status === 501){
                      window.alert(httpRequest.responseText);
                    }
                  }
                }
                httpRequest.open("POST", URL_SERVLET_REGISTER, true);
                httpRequest.setRequestHeader('Content-type','application/x-www-form-urlencoded');
                let login = document.getElementById("login").value;
                let password = document.getElementById("password").value;
                let name = document.getElementById("name").value;
                let cpf = document.getElementById("cpf").value;
                let birth = document.getElementById("birth").value;
                let state = document.getElementById("state").value;
                let city = document.getElementById("city").value;
                let neighborhood = document.getElementById("neighborhood").value;
                let cep = document.getElementById("cep").value;
                let isOng = document.getElementById("isOng").checked;

                
                if(isOng == true){  
                let ongName = document.getElementById("ongName").value;
                

                console.log(login, password, name, cpf, birth, state, neighborhood, cep, isOng, ongName);
                httpRequest.send("login="+login+"&password="+password+"&name="+name+"&cpf="+cpf+"&birth="+birth+"&isOng="+isOng+"&ongName="+ongName+
                "&state="+state+"&city="+city+"&neighborhood="+neighborhood+"&cep="+cep);
                
                }else if (isOng == false){
                console.log(login, password, name, cpf, birth, state, neighborhood, cep, isOng);
                httpRequest.send("login="+login+"&password="+password+"&name="+name+"&cpf="+cpf+"&birth="+birth+"&isOng="+isOng+
                "&state="+state+"&city="+city+"&neighborhood="+neighborhood+"&cep="+cep);
                }      
            }
          
            let cep = document.getElementById("cep");
            let data;
            //PREENCHIMENTO AUTOMATICO DE CAMPOS DE ENDEREÇO
            cep.addEventListener("input", function(){
              let cepValue = cep.value;
              let url = "https://api.postmon.com.br/v1/cep/"+cepValue;
              let httpRequest = new XMLHttpRequest();
              httpRequest.open("GET",url);
              httpRequest.onload = () =>{
                if(httpRequest.status === 200){
                   data = JSON.parse(httpRequest.responseText);
                  let state = document.getElementById("state");
                  let city = document.getElementById("city");
                  let neighborhood = document.getElementById("neighborhood");
                  state.value = data.estado_info.nome;
                  city.value = data.cidade;
                  neighborhood.value = data.bairro;
                }
              }
              httpRequest.send();
            });

          