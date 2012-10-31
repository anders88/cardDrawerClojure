$.urlParam = function(name){
    var results = new RegExp('[\\?&]' + name + '=([^&#]*)').exec(window.location.href);
    return results[1] || 0;
}

$ (function() {
   $.ajaxSetup({ cache: false });
   window.setInterval(function() {
       $.ajax("/score?name=" + $.urlParam("name"),{
           success: function(data, textStatus, jqXHR) {
               $("#someid").html(data);
           }});
   },10000);
});
