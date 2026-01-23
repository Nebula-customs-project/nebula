export default function WarrantyPage() {
  return (
    <main className="max-w-3xl mx-auto py-16 px-4">
      <h1 className="text-3xl font-bold mb-8">Warranty Information</h1>
      <div className="space-y-6">
        <p>All Nebula vehicles come with a comprehensive 5-year or 100,000 km warranty, whichever comes first.</p>
        <ul className="list-disc pl-6 text-gray-700">
          <li>Powertrain coverage</li>
          <li>Battery warranty</li>
          <li>Roadside assistance</li>
          <li>Free annual inspection</li>
        </ul>
        <p>For full details, please contact your local Nebula dealer or visit our <a href="/service-centers" className="text-red-500 underline">Service Centers</a> page.</p>
      </div>
    </main>
  );
}
