// Custom commitlint formatter with colors
function formatter(results) {
  const resultsArray = Array.isArray(results) ? results : [results];
  const red = '\x1b[31m';
  const green = '\x1b[32m';
  const yellow = '\x1b[33m';
  const reset = '\x1b[0m';
  let hasErrors = false;
  let output = '\n';

  resultsArray.forEach(result => {
    // Check top-level error count or results array
    const totalErrors = (result.errorCount || 0) +
      (result.results ? result.results.reduce((sum, r) => sum + (r.errors ? r.errors.length : 0), 0) : 0);

    if (totalErrors > 0 || result.valid === false) {
      hasErrors = true;
      output += `${red}❌ COMMIT FAILED${reset}\n\n`;

      if (result.results) {
        result.results.forEach(r => {
          if (r.errors) {
            r.errors.forEach(err => {
              output += `   • ${err.message}\n`;
            });
          }
          if (r.warnings) {
            r.warnings.forEach(warn => {
              output += `   ${yellow}⚠ ${warn.message}${reset}\n`;
            });
          }
        });
      }

      output += '\n📝 Example:\n';
      output += '   feat: add new feature\n\n';
      output += '🔧 Types:\n';
      output += '   feat • fix • docs • style • refactor\n';
      output += '   perf • test • build • ci • chore\n';
      output += '   revert • docker • db\n\n';
    }
  });

  if (!hasErrors) {
    output = `${green}✓ Commit successful!${reset}\n`;
  }

  return output;
}

module.exports = formatter;
