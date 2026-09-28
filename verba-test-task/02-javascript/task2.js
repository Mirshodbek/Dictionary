// ЗАДАЧА 2: преобразование ответа API в массив строк.

const response = {
  success: true,
  appointments: [
    { id: "A-101", doctor: "Иванов", time: "10:00" },
    { id: "A-102", doctor: "Петров", time: "14:30" }
  ]
};

function toDoctorTimeList(res) {
  // Если API вернул ошибку или список отсутствует, отдаём пустой массив,
  // чтобы сценарий робота не упал.
  if (!res || !res.success || !Array.isArray(res.appointments)) {
    return [];
  }
  return res.appointments.map(a => `${a.doctor} — ${a.time}`);
}

console.log(toDoctorTimeList(response));
// [ 'Иванов — 10:00', 'Петров — 14:30' ]

module.exports = { toDoctorTimeList };
