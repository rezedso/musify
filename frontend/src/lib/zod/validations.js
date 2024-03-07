import { z } from 'zod';

export const registerUserSchema = z.object({
  username: z
    .string()
    .min(3, 'Username must be at least 3 characters long.')
    .max(40, 'Username must be at most 40 characters long.'),
  email: z
    .string()
    .email('Email is not valid.')
    .min(3, 'Email must be at least 3 characters long.')
    .max(80, 'Email must be at most 80 characters long.'),
  password: z
    .string()
    .min(3, 'Password must be at least 3 characters long.')
    .max(80, 'Password must be at most 80 characters long.'),
  image: z
    .custom((value) => value instanceof File, {
      message: 'Invalid input.',
    })
    .optional(),
});

export const loginUserSchema = z.object({
  email: z
    .string()
    .min(12, 'Email must be at least 12 characters long.')
    .max(80, 'Email must be at most 80 characters long.'),
  password: z
    .string()
    .min(3, 'Password must be at least 3 characters long.')
    .max(80, 'Password must be at most 80 characters long.'),
});

export const updateUserSchema = z.object({
  username: z
    .string()
    .min(3, 'Username must be at least 3 characters long.')
    .max(40, 'Username must be at most 40 characters long.'),
  image: z
    .custom((value) => value instanceof File, {
      message: 'Invalid input.',
    })
    .optional(),
});

export const updatePasswordSchema = z.object({
  currentPassword: z
    .string()
    .min(3, 'Current password must be at least 3 characters long.')
    .max(80, 'Current password must be at most 80 characters long.'),
  newPassword: z
    .string()
    .min(3, 'New password must be at least 3 characters long.')
    .max(80, 'New password must be at most 80 characters long.'),
  confirmationPassword: z
    .string()
    .min(3, 'Confirmation password must be at least 3 characters long.')
    .max(80, 'Confirmation password must be at most 80 characters long.'),
});

const commaSeparatedGenresRegex = /^(?!.*,\s,)(?:(?!,\s$).)+$/;

export const genresSchema = z
  .string()
  .refine((value) => commaSeparatedGenresRegex.test(value), {
    message: 'Invalid genres format.',
  });

export const artistSchema = z.object({
  name: z
    .string()
    .min(2, 'Name must be at least 2 characters long.')
    .max(80, 'Name must be at most 80 characters long.'),
  originCountry: z
    .string()
    .min(3, 'Origin Country must be at least 3 characters long.')
    .max(80, 'Origin Country must be at most 80 characters long.'),
  formedYear: z.number().refine((number) => {
    return number <= new Date().getFullYear();
  }, 'Formed year must be less than or equal to the current year.'),
  artistGenres: genresSchema.optional().or(z.literal('')),
  image: z
    .custom(
      (value) => value instanceof File || value === null || value === undefined,
      {
        message: 'Invalid input.',
      }
    )
    .optional(),
});

export const albumSchema = z.object({
  title: z
    .string()
    .min(2, 'Title must be at least 2 characters long.')
    .max(80, 'Title must be at most 80 characters long.'),
  releaseDate: z
    .date()
    .refine((date) => {
      const currentDate = new Date();
      return date <= currentDate;
    }, 'Release date must not be in the future.')
    .optional(),
  albumGenres: genresSchema.optional().or(z.literal('')),
  image: z
    .custom(
      (value) => value instanceof File || value === null || value === undefined,
      {
        message: 'Invalid input.',
      }
    )
    .optional(),
});

export const listSchema = z.object({
  name: z
    .string()
    .min(3, 'Name must be at least 3 characters long.')
    .max(80, 'Name must be at most 80 characters long.'),
});

export const reviewSchema = z.object({
  title: z
    .string()
    .min(3, 'Title must be at least 3 characters long.')
    .max(120, 'Title must be at most 120 characters long.'),
  content: z.string().min(6, 'Content must be at least 6 characters long.'),
  rating: z
    .number()
    .refine((value) => value !== null && value >= 0.5 && value <= 5.0, {
      message: 'Rating is required and must be between 0.5 and 5.0',
    }),
});
