/* Grupo calls */
function editarGrupo() {
   var bodyValores = {
      grupoId: document.querySelector('[name="grupoId"]').value,
      nome: document.querySelector('#nome').value,
      nickname: document.querySelector('#nickname').value,
      descricao: document.querySelector('#descricao').value,
      tipo: document.querySelector('#tipo').value
   }
   api_request('/grupo/editar', bodyValores, null);
}
function criarGrupo() {
   var bodyValores = {
      grupoId: document.querySelector('[name="grupoId"]').value,
      nome: document.querySelector('#nome').value,
      nickname: document.querySelector('#nickname').value,
      descricao: document.querySelector('#descricao').value,
      tipo: document.querySelector('#tipo').value
   }
   api_request('/grupo/criar', bodyValores, null);
}
function adicionarParticipanteAoGrupo() {
   var bodyValores = {
      grupoId: document.querySelector('[name="grupoId"]').value,
      participante: document.querySelector('#participante').value
   }
   api_request('/grupo/adicionar', bodyValores, null);
}

/* Conta calls */
function editarConta() {
   var bodyValores = {
      password: document.querySelector('#password').value,
      senha: document.querySelector('#senha').value
   }
   api_request('/conta/editar', bodyValores, null);
}
function efetuarLogin() {
   var bodyValores = {
      username: document.querySelector('#username').value,
      password: document.querySelector('#password').value
   }
   api_request('/login.js', bodyValores, null);
}
function loginComGoogle(token) {
   var bodyValores = {
      token: token,
   }
   api_request('/login/google', bodyValores, null);
}
function efetuarRegistro() {
   var bodyValores = {
      username: document.querySelector('#username').value,
      password: document.querySelector('#password').value,
      email: document.querySelector('#email').value
   }
   api_request('/registrar', bodyValores, null);
}

/* API request */
function api_request(path, parametro, callback) {
    const http = new XMLHttpRequest();
    http.open('POST', path, true);
    http.setRequestHeader('Content-type', 'application/json');
    http.responseType = 'json';
    http.send(JSON.stringify(parametro));
    http.onload = function(e) {
       if (this.status == 200) {
         var jsonResponse = this.response;

         if(jsonResponse.mensagem != null && jsonResponse.mensagem.length > 0) {
            alert(jsonResponse.mensagem, (jsonResponse.sucess)?'success':'danger');
         }

         if(callback != null) {
            callback(jsonResponse);
         }

         if(jsonResponse.enderecoParaRedirecionar != null) {
            openUrlPath(jsonResponse.enderecoParaRedirecionar);
         }

       } else {
         alert("Ocorreu Um erro na requisição.", 'warning');
       }
    };
}


/* Alertas */
const alert = (message, type) => {
    const wrapper = document.createElement('div')
    wrapper.innerHTML = [
      `<div class="alert alert-${type} alert-dismissible" role="alert">`,
      `   <div>${message}</div>`,
      '   <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>',
      '</div>'
    ].join('')
    const areaDeAlertas = document.getElementById('areaDeAlertas');
    areaDeAlertas.append(wrapper)
}


/* URL calls */
function openUrlPath(pathName) {
    window.location.href = pathName;
}
function goto_url(pathName) {
	var url = window.location.href;
	if(!url.endsWith("/")) {
	url = url + "/";
	}
	url = url + pathName;
	window.location.href = url;
}
function RemoveLastDirectoryPartOf(the_url) {
	var the_arr = the_url.split('/');
	the_arr.pop();
	return( the_arr.join('/') );
}
function openSubUrl() {
	window.location.href = RemoveLastDirectoryPartOf(window.location.href);
}


/* verificar campo input */
function checkCampo(input, regExInput, msgRegEx) {
   if(input.value == null || input.value.length==0) {
      input.setCustomValidity("Verifique o campo.");
   } else {
      if (regExInput !=null && !regExInput.test(input.value)) {
         input.setCustomValidity(((msgRegEx!=null)?msgRegEx:'Não está no formato requerido.')+' Por favor corrigir.');
      } else {
         input.setCustomValidity('');
      }
   }
}