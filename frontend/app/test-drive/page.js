export default function TestDrivePage() {
  return (
    <main className="max-w-3xl mx-auto py-16 px-4">
      <h1 className="text-3xl font-bold mb-8">Book a Test Drive</h1>
      <div className="space-y-6">
        <p>Experience the thrill of driving a Nebula. Fill out the form below to book your test drive.</p>
        <form className="space-y-4 max-w-md">
          <div>
            <label className="block mb-1 font-medium">Name</label>
            <input type="text" className="w-full border rounded px-3 py-2" required />
          </div>
          <div>
            <label className="block mb-1 font-medium">Email</label>
            <input type="email" className="w-full border rounded px-3 py-2" required />
          </div>
          <div>
            <label className="block mb-1 font-medium">Preferred Date</label>
            <input type="date" className="w-full border rounded px-3 py-2" required />
          </div>
          <button type="submit" className="bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600 transition">Book Test Drive</button>
        </form>
      </div>
    </main>
  );
}
