var app = angular.module('RDash', ['ui.bootstrap', 'ui.router', 'ngCookies', 'angularUtils.directives.dirPagination','xeditable']);

app.run(function(editableOptions) {
  editableOptions.theme = 'bs3'; // bootstrap3 theme. Can be also 'bs2', 'default'
});
