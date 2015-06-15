function(key, value) {
    if ('count' in value) {
        return value;
    } else {
        var result = {};
        result.count = 1;
        result.links = [value];
        return result;
    }
}
