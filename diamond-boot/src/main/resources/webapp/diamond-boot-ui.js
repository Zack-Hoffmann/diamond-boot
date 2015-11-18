/* global Mustache */

$(function () {
    // Parse page params
    var params = {
        page: "status"
    };
    var queryString = location.search;
    if (queryString.indexOf("?") === 0) {
        var paramStrings = queryString.substring(1, queryString.length)
                .split("&");
        $.each(paramStrings, function () {
            if (this.indexOf("=") > -1) {
                var splitParamString = this.split("=");
                params[splitParamString[0]] = splitParamString[1];
            }
        });
    }
    
    var content = Mustache.render("<p>The current page is {{page}}.<p>", params);
    $("#content").html(content);

});