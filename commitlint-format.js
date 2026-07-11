// Custom commitlint formatter with colors
function formatter(results) {
  const resultsArray = Array.isArray(results) ? results : [results];
  const red = '\x1b[31m';
  const green = '\x1b[32m';
  const reset = '\x1b[0m';
  let hasErrors = false;
  let output = '\n';

  resultsArray.forEach(result => {
    if (result.errors && result.errors.length > 0) {
      hasErrors = true;
      output += `${red}❌ COMMIT FAILED${reset}\n\n`;

      result.errors.forEach(err => {
        output += `   • ${err.text}\n`;
      });

      output += '\n📝 Example:\n';
      output += '   feat: add new feature\n\n';
      output += '🔧 Types:\n';
      output += '   feat • fix • docs • style • refactor\n';
      output += '   perf • test • build • ci • chore\n';
      output += '   revert • docker • db\n\n';
    }
  });

  if (!hasErrors) {
    output = `${green}Commit successful!${reset}\n`;
  }

  return output;
}

module.exports = formatter;
