function(key, value) {
    if ('links' in value) {
        return value;
    } else {
        var result = {};
        result.point = key;
        result.links = value.links;
        return result;
    }
}
