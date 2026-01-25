console.log("Admin user script");

document.querySelector("#image_file_input")
    .addEventListener("change", function(event){
    let file = event.target.files[0];
    let reader = new FileReader();
    reader.onload = function() {
        document
            .querySelector("#upload_image_preview")
            .setAttribute("src", reader.result.toString());
    }
    reader.readAsDataURL(file);
    console.log(file);
})
