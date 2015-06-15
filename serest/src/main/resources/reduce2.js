function(key, values) {
    var array = [];
    for(var i=0; i<values.length; i++) {
         array = array.concat(values[i].links);
    }

    var unique = [];
    for(var i=0; i<array.length; i++) {
        unique.push(array[i].str);
    }

    var unique = [];
    unique = array.filter(function(item, pos) {
        return array.indexOf(item) == pos & item != key.str;
    });

    return {point:key.str, links:unique};
}