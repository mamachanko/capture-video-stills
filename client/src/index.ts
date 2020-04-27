const video: HTMLVideoElement = document.getElementById(
  'video',
) as HTMLVideoElement;

video.currentTime = 9.5;

const button = document.getElementById('button') as HTMLButtonElement;

const image = document.getElementById("img") as HTMLImageElement;
image.src = "/api/image.png";

button.addEventListener('click', () => {
  const canvas = document.createElement('canvas');
  canvas.width = video.videoWidth;
  canvas.height = video.videoHeight;
  const ctx = canvas.getContext('2d') as CanvasRenderingContext2D;

  ctx.drawImage(video, 0, 0, canvas.width, canvas.height);

  const dataURI = canvas.toDataURL('image/png');

  console.log(dataURI);

  const capture = document.getElementById('capture') as HTMLDivElement;

  const img = document.createElement('img');
  img.src = dataURI;

  capture.innerHTML = '';
  capture.appendChild(img);

  const sendButton = document.createElement('button') as HTMLButtonElement;
  sendButton.innerText = 'send';
  sendButton.addEventListener('click', () => {


    var blobBin = atob(dataURI.split(',')[1]);
    var array = [];
    for(var i = 0; i < blobBin.length; i++) {
      array.push(blobBin.charCodeAt(i));
    }
    var file = new Blob([new Uint8Array(array)], {type: 'image/png'});

    const formData = new FormData();
    formData.append("file", file, "screen-capture.png");

    console.log(file)
    console.log(formData)

    fetch('/api/images', {
      method: 'POST',
      body: formData
    });

  });
  capture.appendChild(sendButton);

  console.log(dataURI.length);
});
