export default function ServiceCentersPage() {
  return (
    <main className="max-w-3xl mx-auto py-16 px-4">
      <h1 className="text-3xl font-bold mb-8">Service Centers</h1>
      <div className="space-y-6">
        <p>Nebula service centers are located in major cities across Europe. Our certified technicians provide expert care for your vehicle.</p>
        <ul className="list-disc pl-6 text-gray-700">
          <li>Stuttgart, Germany</li>
          <li>Munich, Germany</li>
          <li>Paris, France</li>
          <li>Milan, Italy</li>
          <li>Amsterdam, Netherlands</li>
        </ul>
        <p>Book a service appointment via your <a href="/my-nebula-car" className="text-red-500 underline">User Dashboard</a>.</p>
      </div>
    </main>
  );
}
