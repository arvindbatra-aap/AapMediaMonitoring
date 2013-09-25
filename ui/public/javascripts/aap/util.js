function empty(data)
{
  if(typeof(data) == 'number' || typeof(data) == 'boolean') {
    return false;
  }

  if(typeof(data) == 'undefined' || data === null) {
    return true;
  }

  if(typeof(data.length) != 'undefined') {
    return data.length == 0;
  }

  for(var i in data) {
    if(data.hasOwnProperty(i)) {
      return false;
    }
  }

  return true;
}

function removeHC() {
  $('.highcharts-container > svg > text:last-child').last().remove();
}