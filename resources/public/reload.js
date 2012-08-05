 $(document).ready(function() {
   var refreshId = setInterval(function() {
      $("#someid").load('update.html');
   }, 3000);
   $.ajaxSetup({ cache: false });
});
