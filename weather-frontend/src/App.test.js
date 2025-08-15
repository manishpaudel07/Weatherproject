import { render, screen } from '@testing-library/react';
import App from './App';

describe('App', () => {
  test('renders weather app title', () => {
    render(<App />);
    const titleElement = screen.getByRole('heading', { name: /weather app/i });
    expect(titleElement).toBeInTheDocument();
  });

  test('renders city input', () => {
    render(<App />);
    const inputElement = screen.getByPlaceholderText(/enter city/i);
    expect(inputElement).toBeInTheDocument();
  });

  test('renders get weather button', () => {
    render(<App />);
    const buttonElement = screen.getByRole('button', { name: /get weather/i });
    expect(buttonElement).toBeInTheDocument();
  });
});
