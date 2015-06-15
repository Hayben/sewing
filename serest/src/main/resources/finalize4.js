function(key, value) {
//    if ('links' in value) {
//        return value;
//    } else {
//        var result = {};
//        result.title = key;
//        result.links = [value];
//        return result;
//    }
    if ('links' in value) {
        return value.links;
    } else {
        var result = [];
        result.push(value);
        return result;
    }
}