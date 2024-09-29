'use client'

import { useRouter } from 'next/navigation';
import { useEffect } from 'react';

const RedirectPage = ({ params }: { params: { uuid: string, name: string } }) => {
  const router = useRouter();
  const { uuid, name } = params;

  useEffect(() => {
    const redirectToFile = () => {
      if (uuid && name) {
        setTimeout(() => {
          const finalUrl = `http://localhost:8080/${uuid}/${name}`;
          router.push(finalUrl);
        }, 500);
      }
    };

    redirectToFile();
  }, [uuid, name, router]);

  return (
    <div style={{
      display: 'flex',
      justifyContent: 'center',
      alignItems: 'center',
      height: '100vh',
      textAlign: 'center'
    }}>
      <div className="text-lg font-black">
        <h1 className="animate-in">Building your file...</h1>
        <p>Please wait while we prepare your download.</p>
      </div>
    </div>
  );
};

export default RedirectPage;
