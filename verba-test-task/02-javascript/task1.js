// ЗАДАЧА 1: формирование текста напоминания для робота.

const appointment = {
  patientName: "Анна",
  doctor: "Иванов Сергей Петрович",
  specialty: "терапевт",
  date: "2026-10-12",
  time: "15:30"
};

// "2026-10-12" -> "12.10.2026".
// Разбираем строку вручную, а не через new Date(), чтобы часовой пояс
// сервера не мог сдвинуть дату на день.
function formatDate(isoDate) {
  const [year, month, day] = isoDate.split("-");
  return `${day}.${month}.${year}`;
}

function buildReminderText(a) {
  return `${a.patientName}, вы записаны к врачу ${a.doctor}, ${a.specialty}, на ${formatDate(a.date)} в ${a.time}.`;
}

console.log(buildReminderText(appointment));
// Анна, вы записаны к врачу Иванов Сергей Петрович, терапевт, на 12.10.2026 в 15:30.

module.exports = { formatDate, buildReminderText };
