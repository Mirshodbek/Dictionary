// Проверка решений: node 02-javascript/test.js
const assert = require("node:assert");
const { buildReminderText, formatDate } = require("./task1");
const { toDoctorTimeList } = require("./task2");

assert.strictEqual(
  buildReminderText({
    patientName: "Анна",
    doctor: "Иванов Сергей Петрович",
    specialty: "терапевт",
    date: "2026-10-12",
    time: "15:30"
  }),
  "Анна, вы записаны к врачу Иванов Сергей Петрович, терапевт, на 12.10.2026 в 15:30."
);
assert.strictEqual(formatDate("2026-01-05"), "05.01.2026");

assert.deepStrictEqual(
  toDoctorTimeList({
    success: true,
    appointments: [
      { id: "A-101", doctor: "Иванов", time: "10:00" },
      { id: "A-102", doctor: "Петров", time: "14:30" }
    ]
  }),
  ["Иванов — 10:00", "Петров — 14:30"]
);
assert.deepStrictEqual(toDoctorTimeList({ success: false }), []);
assert.deepStrictEqual(toDoctorTimeList({ success: true, appointments: [] }), []);

console.log("Все проверки пройдены");
