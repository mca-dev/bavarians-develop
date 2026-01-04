(function(){
    console.log("Hello World!");
})();

$(document).ready(function() {
var max_fields      = 100; //maximum input boxes allowed
var wrapper   		= $(".bavarians_oferty_input_fields_wrap"); //Fields wrapper
var add_button      = $(".add_field_button"); //Add button ID

var x = 0; //initlal text box count
$(add_button).click(function(e){ //on add input button click
e.preventDefault();
    if(x < max_fields){ //max input box allowed
        x++; //text box increment
        $(wrapper).append('<tr> <td><input type="text"  class="form-control" id="elementySerwisowe'+x+'.nazwa" name="elementySerwisowe['+x+'].nazwa">  </td>  <td>      <input type="text" pattern="^\\d*(\\.\\d{0,2})?$" class="form-control" id="elementySerwisowe'+x+'.cenaRobociznyNetto" name="elementySerwisowe['+x+'].cenaRobociznyNetto">   </td>  <td>          <input type="text" pattern="^\\d*(\\.\\d{0,2})?$" class="form-control" id="elementySerwisowe'+x+'.cenaCzesciBrutto" name="elementySerwisowe['+x+'].cenaCzesciBrutto"> </td>  <td>                <button class="btn btn-outline-danger remove_field" type="button">&#xd7;</button></td> </tr>');
    }
});

$(wrapper).on("click",".remove_field", function(e){ //user click on remove text
    e.preventDefault(); $(this).parent('td').parent('tr').remove(); x--;
    })
});


$(document).ready(function() {

var max_fields      = 100; //maximum input boxes allowed
var wrapper   		= $(".bavarians_oferty_input_fields_wrap_edycja"); //Fields wrapper

var add_button      = $(".add_field_button_edycja"); //Add button ID

var x = wrapper.children().length-1;
$(add_button).click(function(e){ //on add input button click
e.preventDefault();
    if(x < max_fields){ //max input box allowed
        x++; //text box increment
        $(wrapper).append('<tr> <td><input type="text"  class="form-control" id="elementySerwisowe'+x+'.nazwa" name="elementySerwisowe['+x+'].nazwa">  </td>  <td>      <input type="text" pattern="^\\d*(\\.\\d{0,2})?$" class="form-control" id="elementySerwisowe'+x+'.cenaRobociznyNetto" name="elementySerwisowe['+x+'].cenaRobociznyNetto">   </td>  <td>          <input type="text" pattern="^\\d*(\\.\\d{0,2})?$" class="form-control" id="elementySerwisowe'+x+'.cenaCzesciBrutto" name="elementySerwisowe['+x+'].cenaCzesciBrutto"> </td>  <td>                <button class="btn btn-outline-danger remove_field" type="button">&#xd7;</button></td> </tr>');
    }
});

$(wrapper).on("click",".add_below_field", function(e){ //user click on remove text
        x =  $(this).parent('td').parent('tr').index()+1 ;
        alert(x)
        e.preventDefault();
        $(this).parent('td').parent('tr:eq(' +  $(this).index() + ')').after('<tr> <td><input type="text"  class="form-control" id="elementySerwisowe'+x+'.nazwa" name="elementySerwisowe['+x+'].nazwa">  </td>  <td>      <input type="text" pattern="^\\d*(\\.\\d{0,2})?$" class="form-control" id="elementySerwisowe'+x+'.cenaRobociznyNetto" name="elementySerwisowe['+x+'].cenaRobociznyNetto">   </td>  <td>          <input type="text" pattern="^\\d*(\\.\\d{0,2})?$" class="form-control" id="elementySerwisowe'+x+'.cenaCzesciBrutto" name="elementySerwisowe['+x+'].cenaCzesciBrutto"> </td>  <td>                <button class="btn btn-outline-danger remove_field" type="button">&#xd7;</button></td> </tr>');
    });

});