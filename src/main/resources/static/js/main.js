/* Global var */
const usuarioFormato = /^([a-z0-9_-]+)$/;
const emailFormato = /^([a-z0-9._-]+)$/;
const tempoDelayParaRedirecionar = 1300;

/* Grupo calls */
function adicionarParticipanteAoGrupo(grupoId, participante, callback) {
   var bodyValores = {
      grupoId: grupoId,
      participante: participante,
   }
   api_request('/grupo/participante/adicionar', bodyValores, callback);
}
function removerParticipanteDoGrupo(grupoId, participante, callback) {
   var bodyValores = {
      grupoId: grupoId,
      participante: participante,
   }
   api_request('/grupo/participante/remover', bodyValores, callback);
}
function entrarNoGrupo(grupoId, callback) {
   var bodyValores = {
      grupoId: grupoId,
   }
   api_request('/grupo/participante/entrar', bodyValores, callback);
}
function removerSubGrupo(grupoId, grupoIdRemover, callback) {
   var bodyValores = {
      grupoId: grupoId,
      grupoIdRemover: grupoIdRemover,
   }
   api_request('/grupo/remover', bodyValores, callback);
}

/* Conta calls */
function handleCredentialResponse(response) {
    var bodyValores = {
       token: response.credential,
    }
    api_request('/login/google', bodyValores, null);
}

/* Imagem Upload */
function cunfigurarUploadDeImagem(imagem_input) {
    if(imagem_input) {
        imagem_input.addEventListener("change", function () {
            var $files = imagem_input.files;
            if ($files.length) {
              uploadDaImagem($files[0]);
            }
        }, false);
    }
}
function configurarUploadDeImagemComCrop() {
    //<!-- Crop Image Start -->
    let result = document.querySelector('.result'),
    boxCortar = document.querySelector('.boxCortar'),
    cortarBt = document.querySelector('.cortarBt'),
    cropped = document.querySelector('.cropped'),
    upload = document.querySelector('#imagem_file'),
    cropper = '';
    upload.addEventListener('change', (e) => {
        if (e.target.files.length) {
            const reader = new FileReader();
            reader.onload = (e)=> {
                if(e.target.result){
                     $('#cropModalCenter').on('shown.bs.modal', function (ee) {
                         let img = document.createElement('img');
                         img.id = 'imageCrop';
                         img.src = e.target.result
                         result.innerHTML = '';
                         result.appendChild(img);
                         cropper = new Cropper(img, {aspectRatio: 1 / 1,});
                     });
                     $('#cropModalCenter').modal('show');
                }
            };
            reader.readAsDataURL(e.target.files[0]);
        }
    });
    cortarBt.addEventListener('click',(e)=>{
        $('#cropModalCenter').modal('hide');
        cropper.getCroppedCanvas().toBlob((blob) => {
              var imageFile = new File([blob], "image.jpeg", {type: "image/jpeg",lastModified: new Date(0)});
              // comprimir imagem
              new Compressor(imageFile, {
                  quality: 0.5,
                  success(result) {
                      uploadDaImagem(result);
                  },
                  error(err) {
                      alert("Ocorreu um erro ao comprimir a imagem.", 'warning');
                  },
              });
        });
    });
    //<!-- Crop Image End -->
}
function uploadDaImagem(file) {
    if (!file || !file.type.match(/image.*/)) return;
    var fd = new FormData();
    fd.append("imagem", file);
    var http = new XMLHttpRequest();
    http.open("POST", "/imagem/upload");
    http.responseType = 'json';


    var button = document.querySelectorAll("button[type=submit]")[0];
    var titleOrig = button.textContent;
    button.setAttribute('disabled', 'disabled');
    button.textContent = 'Carregando Imagem...';
    http.onload = function(e) {
        button.removeAttribute('disabled');
        button.textContent = titleOrig;
        if (this.status == 200) {
            try {
                var jsonResp = this.response;
                if(jsonResp.mensagem != null && jsonResp.mensagem.length > 0) {
                   alert(jsonResp.mensagem, (jsonResp.sucess)?'success':'danger');
                }
                if(jsonResp.sucess) {
                    var link = jsonResp.conteudo.link;
                    document.getElementById("imagemUp").src = link;
                    document.getElementById("imagemUrl").value = link;
                }
            } catch (error) {
                alert(error, 'warning');
            }
        } else {
            alert("Ocorreu um erro na requisição.", 'warning');
        }
    }
    http.onerror = function () {
        button.removeAttribute('disabled');
        button.textContent = titleOrig;
        alert("Ocorreu um erro na requisição.", 'warning');
    }
    http.send(fd);
}

/* Form Request Body Parse */
function sendForm(formId, path, callback) {
    var form = document.getElementById(formId);
    var arr = $(form).serializeArray(), names = (function(){
        var n = [],
            l = arr.length - 1;
        for(; l>=0; l--){
            n.push(arr[l].name);
        }
        return n;
    })();
    $('#'+formId+' input[type="checkbox"]:not(:checked)').each(function(){
        if($.inArray(this.name, names) === -1){
            arr.push({name: this.name, value: false});
        }
    });
    var dataBodyDic = arr.reduce(function(obj, item) {
        var valueItem = item.value;
        if(valueItem == 'on') {
            valueItem = true;
        }
        obj[item.name] = valueItem;
        return obj;
    }, {});
    api_request(path, dataBodyDic, callback);
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
            setTimeout(function () {
                openUrlPath(jsonResponse.enderecoParaRedirecionar);
            }, tempoDelayParaRedirecionar);
         }

       } else {
         alert("Ocorreu um erro na requisição.", 'warning');
       }
    };
    http.onerror = function () {
        alert("Ocorreu um erro na requisição.", 'warning');
    }
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
    if(areaDeAlertas) {
        areaDeAlertas.append(wrapper);
    }
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
    if(input != null) {
        if(input.value == null || input.value.length==0) {
            input.setCustomValidity("Verifique o campo.");
        } else {
            if (regExInput !=null && !regExInput.test(input.value)) {
                input.setCustomValidity(((msgRegEx!=null)?msgRegEx:'Não está no formato requerido.')+' Por favor corrigir.');
            } else {
                input.setCustomValidity('');
                return true;
            }
        }
    }
    return false;
}

/* Auto Complete, Pesquisar Usuário ou Grupo */
function configurarAutoCompInputId(inputId, clickedFieldCallback, filtroArr) {
    if(document.getElementById(inputId) == null) {
        return;
    }
	$("#"+inputId).autocomplete({
		source: function (request, response) {
           $.ajax({
               type: 'POST',
               url: '/pesquisar',
               data: JSON.stringify({
                    term: request.term,
                    filtro: filtroArr?filtroArr:[],
               }),
               success: response,
               dataType: 'json',
               contentType: 'application/json',
           });
        },
        minLength: 2,
        delay: 800,
        select: function( event, ui ) {
            if(clickedFieldCallback) {
                clickedFieldCallback(ui);
            }
        }
	}).autocomplete( "instance" )._renderItem = function( ul, item ) {
        return $( "<li><div><img class='img-thumbnail' src='"+item.img+"' width='50' height='50'>  <span>"+item.value+"</span></div></li>" ).appendTo( ul );
    };
}