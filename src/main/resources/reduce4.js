function(key, values) {
    var unique = [];
    unique = values.filter(function(item, pos) {
        return values.indexOf(item) == pos & item != key;
    });

    return {title:key, links:unique};
}
