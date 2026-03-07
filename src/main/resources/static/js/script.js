console.log("Script loaded");

let currentTheme = getTheme();
// initially changeTheme called automatically
// changeTheme();

// call changeTheme() only when web page is loaded properly
document.addEventListener("DOMContentLoaded", () => {
    changeTheme();
    highlightActiveNavLink();
});

// Highlight active navigation link
function highlightActiveNavLink() {
    const currentPath = window.location.pathname;
    const navLinks = document.querySelectorAll('.nav-link');
    
    navLinks.forEach(link => {
        const linkPath = new URL(link.href).pathname;
        if (linkPath === currentPath) {
            link.classList.remove('text-gray-900');
            link.classList.add('text-blue-700', 'md:text-blue-700', 'md:dark:text-blue-500');
        }
    });
}

// TODO:
function changeTheme() {

    console.log(currentTheme);
changePageTheme(currentTheme, currentTheme);
    // set the listener to change theme button
    const changeThemeButton = document.querySelector('#theme_change_button');

    // listen theme button event
    changeThemeButton.addEventListener("click", (event) => {
        console.log("change theme button clicked");
        const oldTheme = currentTheme;
        if(oldTheme == "dark") {
            currentTheme = "light";
        } else {
            currentTheme = "dark";
        }
        changePageTheme(oldTheme, currentTheme);
    });
}

// set theme to localstorage
function setTheme(theme) {
    localStorage.setItem("theme", theme);
}

// get theme from localstorage
function getTheme() {
    let theme = localStorage.getItem("theme");
    return theme ? theme : "light"; // by default we're returning light theme
}

function changePageTheme(oldTheme, currentTheme) {
    // update the theme in localStorage
        setTheme(currentTheme);
        // remove the old theme from web page
        document.querySelector("html").classList.remove(oldTheme);
         // add the current theme to web page
        document.querySelector("html").classList.add(currentTheme);
        // change the text of the theme button
    document.querySelector('#theme_change_button').querySelector("span").textContent = currentTheme == "light" ? "Dark" : "Light";
}