$(function () {
    var buttons = [
        {title: "Block (trap)", type: "block"},
        {title: "Block (trap+animated)", type: "block_animated"},
        {title: "Block (solid)", type: "block_physical"},
        {title: "Block (solid+animated)", type: "block_physical_animated"},
        {title: "Light", type: "light"},
        {title: "Coffin", type: "coffin"},
        {title: "Live", type: "life"},
        {title: "Portal", type: "portal"},
        {title: "Fountain", type: "fountain"},
        {title: "Shop", type: "shop"},
        {title: "Goal", type: "goal"},
        {title: "Tower", type: "tower"}
    ];
    var grid = $("#grid tbody");
    var selection = null;
    var mousedown = false;
    var lastSelection = null;
    var $y = $("#y");

    grid.on("mousedown", function () {
        mousedown = true;
    });

    grid.on("mouseup", function () {
        mousedown = false;
        lastSelection = null;
    });

    function datetime() {
        var date = new Date();
        var datevalues = [
            date.getFullYear(),
            date.getMonth() + 1,
            date.getDate(),
            date.getHours(),
            date.getMinutes(),
            date.getSeconds()
        ];
        return datevalues.join("_");
    }

    buttons.forEach(function (button) {
        $("#tools").prepend('<label class="' + button.type + '"><input type="radio" name="selection" value="' + button.type + '">' + button.title + '</label>')
    });

    $('#tools input').on("change", function (event) {
        selection = $(event.target).val();
    });

    function serialize() {
        var data = [];

        Array.prototype.forEach.call(document.querySelectorAll("#grid td"), function (el) {
            if (el.getAttribute("class")) {
                data.push({
                    "type": el.getAttribute("class").split(" "),
                    "x": el.getAttribute("data-x"),
                    "z": el.getAttribute("data-z"),
                    "y": el.getAttribute("data-y")
                });
            }
        });

        return JSON.stringify(data);
    }

    $("#export").on("click", function () {
        $("textarea").text(serialize());
    });

    var reader = new FileReader();
    reader.onload = function (ev) {
        var contents = JSON.parse(ev.target.result);
        var sortX = contents.map(function (el) {
            return el.x;
        }).sort();
        var sortZ = contents.map(function (el) {
            return el.z
        }).sort();

        console.log(sortX[0], sortX[sortX.length - 1], sortZ[0], sortZ[sortZ.length - 1]);

        draw();
        contents.forEach(function (element) {
            var selector = '#grid td[data-x="' + element.x + '"][data-y="0"][data-z="' + element.z + '"]';
            var $el = $(selector);
            if (!document.querySelector(selector)) {
                console.log("missing: " + selector)
            }
            if ($el) {
                $el.attr({
                    "class": element.type.join(" "),
                    "data-y": element.y
                }).text(element.y);
            }
        });
    };

    $("#load:file").on("change", function () {
        var file = this.files[0];
        reader.readAsText(file);
    });

    $("#save").on("click", function () {
        var file = new File([serialize()], "level_" + datetime() + ".json", {type: "application/json;charset=utf-8"});
        saveAs(file);
    });

    $("#draw").on("click", draw);

    function toggle(target) {
        if (lastSelection !== target) {
            var $t = $(target);
            lastSelection = target;
            var clazz = $t.attr("class");
            if (clazz) {
                // Max two kinds per cell
                var classes = clazz.split(" ");
                if (classes.length > 2) {
                    return;
                }
            }
            $t.toggleClass(selection);
            if ($t.attr("class") === "") {
                $t.text(0);
                $t.attr("data-y", 0);
            } else {
                $t.text($y.val());
                $t.attr("data-y", $y.val());
            }
        }
    }

    grid.on("click", function (event) {
        if (selection !== null && event.target.tagName === 'TD') {
            toggle(event.target);
        }
    });

    grid.on("mousemove", function (event) {
        if (mousedown && selection !== null && event.target.tagName === 'TD') {
            toggle(event.target);
        }
    });

    function draw() {
        var xSize = $("#x").val();
        var zSize = $("#z").val();

        var html = "";
        for (var z = zSize; z >= -zSize; z -= 1) {
            html += "<tr>";
            for (var x = -xSize; x <= xSize; x += 1) {
                var classVal = "";
                var content = "0";
                if (x === 0 && z === 0) {
                    classVal = 'class="block_physical" ';
                    content = x + "," + z;
                }
                html += '<td ' + classVal + ' data-x="' + x + '" data-z="' + z + '" data-y="0">' + content + '</td>';
            }
            html += "</tr>";
        }
        grid.html(html);
    }

    draw();
});
