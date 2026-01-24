export default function FAQPage() {
  return (
    <main className="max-w-3xl mx-auto py-16 px-4">
      <h1 className="text-3xl font-bold mb-8">Frequently Asked Questions</h1>
      <div className="space-y-6">
        <div>
          <h2 className="text-xl font-semibold mb-2">How do I book a test drive?</h2>
          <p>Visit our <a href="/test-drive" className="text-red-500 underline">Test Drive</a> page and fill out the booking form.</p>
        </div>
        <div>
          <h2 className="text-xl font-semibold mb-2">Where are your service centers located?</h2>
          <p>See our <a href="/service-centers" className="text-red-500 underline">Service Centers</a> page for a full list of locations.</p>
        </div>
        <div>
          <h2 className="text-xl font-semibold mb-2">What warranty do you offer?</h2>
          <p>All Nebula vehicles come with a 5-year/100,000 km warranty. Details are on our <a href="/warranty" className="text-red-500 underline">Warranty</a> page.</p>
        </div>
        <div>
          <h2 className="text-xl font-semibold mb-2">How do I contact support?</h2>
          <p>Use our <a href="/contact-us" className="text-red-500 underline">Contact Us</a> page to reach our support team.</p>
        </div>
      </div>
    </main>
  );
}
