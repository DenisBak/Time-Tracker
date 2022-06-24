
    document.getElementById("date").value = getDate();
    function getDate() {
        let dateJSON = new Date().toJSON();

        const timeParts = dateJSON.split('T');
        return timeParts[0];
    }