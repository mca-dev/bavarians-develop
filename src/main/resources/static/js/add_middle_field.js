$(document).ready(function() {
    var max_fields      = 500;
    var wrapper   		= $(".bavarians_oferty_input_fields_wrap");
    var last_index = wrapper.children().length-1;

    $(".add_field_button").click(function(e){
        e.preventDefault();
        if(last_index < max_fields){
            last_index++;
            $(wrapper).append(getTr(last_index));
        }
    });

    var current_index = 0;
    $(wrapper).on("click",".add_below_field", function(e){
            e.preventDefault();
            current_index =  $(this).parent('td').parent('tr').index() ;
            var next_index = current_index + 1;
            $(this).parent('td').parent('tr').after(getTr(next_index));

            $(this).parent('td').parent('tr').parent().children('tr:gt('+ next_index +')').each(function( i ) {

                $(this).html(getRowWithNewIndex($(this), $(this).index()));

              });
        });



    $(wrapper).on("click",".remove_field", function(e){
            e.preventDefault();
            var current_index =  $(this).parent('td').parent('tr').index() ;

            $(this).parent('td').parent('tr').parent().children('tr:gt('+ current_index +')').each(function( i ) {
                $(this).html(getRowWithNewIndex($(this), $(this).index() - 1));
            });

            $(this).parent('td').parent('tr').remove();
            last_index--;
        })

    function getRowWithNewIndex(row, new_index) {
      var nazwa = row.children('td').children('input:eq(0)').val();
      var net = row.children('td').children('input:eq(1)').val();
      var brut = row.children('td').children('input:eq(2)').val();
      return getTrContent(new_index, nazwa, net, brut);
    }

    function getTr(x) {
      var pwd_pattern = 'pattern="^\\d*(\\.\\d{0,2})?$"';
      var class_form = 'class="form-control"';
      var rem_btn = '<button class="btn btn-outline-danger remove_field" type="button">&#xd7;</button>';
      var add_btn = '<button class="btn add_below_field" type="button">&#x21A9;</button>';
      var result = '<tr>'+
                        '<td><input type="text" required '+ class_form +' id="elementySerwisowe'+x+'.nazwa" name="elementySerwisowe['+ x +'].nazwa"></td>' +
                        '<td><input type="text" '+ pwd_pattern +' '+ class_form +' id="elementySerwisowe'+ x +'.cenaRobociznyNetto" name="elementySerwisowe['+x+'].cenaRobociznyNetto"></td>' +
                        '<td><input type="text"'+ pwd_pattern +' '+ class_form +' id="elementySerwisowe'+ x +'.cenaCzesciBrutto" name="elementySerwisowe['+x+'].cenaCzesciBrutto"></td>' +
                         '<td>'+ rem_btn +'</td>'+
                         '<td>'+ add_btn +'</td>'+
                  '</tr>';
      return result;
    }

    function getTrContent(x, nazwa, roboNet, roboBrut) {
      var pwd_pattern = 'pattern="^\\d*(\\.\\d{0,2})?$"';
      var class_form = 'class="form-control"';
      var rem_btn = '<button class="btn btn-outline-danger remove_field" type="button">&#xd7;</button>';
      var add_btn = '<button class="btn add_below_field" type="button">&#x21A9;</button>';

      var result =      '<td><input type="text" required '+ class_form +' id="elementySerwisowe'+ x +'.nazwa" name="elementySerwisowe['+ x +'].nazwa" value="' + nazwa + '" ></td>' +
                        '<td><input type="text" '+ pwd_pattern +' '+ class_form +' id="elementySerwisowe'+ x +'.cenaRobociznyNetto" name="elementySerwisowe['+x+'].cenaRobociznyNetto" value="' + roboNet + '" ></td>' +
                        '<td><input type="text"'+ pwd_pattern +' '+ class_form +' id="elementySerwisowe'+ x +'.cenaCzesciBrutto" name="elementySerwisowe['+x+'].cenaCzesciBrutto" value="' + roboBrut + '" ></td>' +
                        '<td>'+ rem_btn +'</td>'+
                        '<td>'+ add_btn +'</td>';
      return result;
    }




});