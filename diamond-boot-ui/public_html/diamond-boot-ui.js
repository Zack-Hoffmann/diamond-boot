/* global Mustache */

$(function () {

    var model = {
        params: {
            page: "status"
        },
        pages: {
            status: {
                resources: {
                    status: {
                        url: "http://localhost:8080/services/status"
                    }
                }
            }
        }
    };

    var parseParameters = function (model) {
        var def = $.Deferred();
        var queryString = location.search;
        if (queryString.indexOf("?") === 0) {
            var paramStrings = queryString.substring(1, queryString.length)
                    .split("&");
            $.each(paramStrings, function () {
                if (this.indexOf("=") > -1) {
                    var splitParamString = this.split("=");
                    model.params[splitParamString[0]] = splitParamString[1];
                }
            });
        }
        def.resolve(model);
        return def.promise();
    };

    var loadResources = function (model) {
        var def = $.Deferred();

        var getResourceHandler = function (resource) {
            var res = resource;
            return function (d) {
                var def = $.Deferred();
                res.data = d;
                def.resolve();
                return def;
            };
        };

        var resourceCalls = [];

        $.each(model.pages, function () {
            $.each(this.resources, function () {
                var requestPromise = $.get(this.url);
                requestPromise.then(getResourceHandler(this)).then(function(){
                    console.log("One Done!");
                });
                resourceCalls.push(requestPromise);
            });
        });

        $.when.apply(null, resourceCalls).then(function () {
            console.log("All done!");
            def.resolve(model);
        });

        return def.promise();
    };

    var renderPage = function (model) {
        var def = $.Deferred();
        var content = Mustache.render("<p>The current page is {{params.page}}.<p>", model);
        $("#content").html(content);
        def.resolve(model);
        return def.promise();
    };

    var logData = function (model) {
        var def = $.Deferred();
        console.log(JSON.stringify(model));
        def.resolve(model);
        return def.promise();
    };

    parseParameters(model)
            .then(loadResources)
            .then(renderPage)
            .then(logData);
});