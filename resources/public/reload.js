 $(document).ready(function() {
   var refreshId = setInterval(function() {
      $("#someid").load('update.html?name=' + document.getElementById('namediv').innerHTML);
   }, 3000);
   $.ajaxSetup({ cache: false });
});
