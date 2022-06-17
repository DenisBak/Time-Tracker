function getCookie(name) {
  let matches = document.cookie.match(new RegExp(
      "(?:^|; )" + name.replace(/([\.$?*|{}\(\)\[\]\\\/\+^])/g, '\\$1') + "=([^;]*)"));
  return matches ? decodeURIComponent(matches[1]) : undefined;
}

const cookieValue = getCookie("SID");
if (typeof cookieValue === "undefined") {
  document.getElementById("workspaceLink").style.display = "none";
  document.getElementById("logoutLink").style.display = "none";
} else {
  document.getElementById("loginLink").style.display = "none";
  document.getElementById("logoutLink").style.display = "none";
  document.getElementById("regLink").style.display = "none";
}
