        var row;
        function start(){
          row = event.target;
        }
        function dragover(){
          var e = event;
          e.preventDefault();

          let children= Array.from(e.target.parentNode.parentNode.children);
          if(children.indexOf(e.target.parentNode)>children.indexOf(row)){
                e.target.parentNode.after(row);
          } else{
                e.target.parentNode.before(row);

          }

         var innd = $(e.target.parentNode).index();

         $(e.target.parentNode.children).each(function( i ) {
                console.log($(this).html());
                $(this).children().each(function( j ) {
                    var varName =  $(this).attr("name");

                    if (typeof(varName) !== 'undefined') {
                         var oldName =  varName.split(".");
                         var result = oldName[1];

                         $(this).attr("id",  "elementySerwisowe" + innd + "." + result);
                         $(this).attr("name",  "elementySerwisowe[" + innd + "]." + result);
                    }


                 });
          });



        }