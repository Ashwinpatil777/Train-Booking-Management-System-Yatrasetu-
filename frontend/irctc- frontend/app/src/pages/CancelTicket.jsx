import React, { useState } from 'react';
import api from '../services/api';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faTrashAlt, faInfoCircle } from '@fortawesome/free-solid-svg-icons';

const CancelTicket = () => {
  const [pnr, setPnr] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const handleCancel = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    if (!pnr.trim()) {
      setError('Please enter a PNR number.');
      return;
    }

    setLoading(true);

    try {
      // Assuming the backend endpoint is POST /api/bookings/cancel/{pnr}
      await api.delete(`/api/bookings/cancel/${pnr}`);
      setSuccess(`Booking with PNR ${pnr} has been successfully cancelled.`);
      setPnr('');
    } catch (err) {
      if (err.response?.status === 404) {
        setError('Booking not found with the provided PNR.');
      } else if (err.response?.data?.message) {
        setError(`Failed to cancel booking: ${err.response.data.message}`);
      } else {
        setError('An unexpected error occurred on the server. Please check the backend logs.');
      }
    }
    setLoading(false);
  };

  return (
    <div className="container mt-5">
      <div className="row justify-content-center">
        <div className="col-md-6">
          <div className="card">
            <div className="card-header">
              <h4>Cancel Ticket</h4>
            </div>
            <div className="card-body">
              <form onSubmit={handleCancel}>
                <div className="mb-3">
                  <label htmlFor="pnrInput" className="form-label">Enter PNR Number</label>
                  <input
                    type="text"
                    id="pnrInput"
                    className="form-control"
                    placeholder="Enter PNR to cancel"
                    value={pnr}
                    onChange={(e) => setPnr(e.target.value.toUpperCase())}
                  />
                </div>
                {error && (
                  <div className="alert alert-danger">
                    <FontAwesomeIcon icon={faInfoCircle} className="me-2" />
                    {error}
                  </div>
                )}
                {success && (
                  <div className="alert alert-success">
                    <FontAwesomeIcon icon={faInfoCircle} className="me-2" />
                    {success}
                  </div>
                )}
                <div className="d-grid">
                  <button type="submit" className="btn btn-danger" disabled={loading}>
                    {loading ? 'Cancelling...' : (
                      <><FontAwesomeIcon icon={faTrashAlt} className="me-2" />Cancel Ticket</>
                    )}
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default CancelTicket;
