namespace Pleisto.Flappy.Utils
{
    /// <summary>
    /// Generic payload type packer
    /// </summary>
    /// <typeparam name="T">call reference</typeparam>
    public class EasyPayload<T>
    {
        /// <summary>
        /// Payload
        /// </summary>
        public T Payload { get; set; }

        /// <summary>
        /// Fast convert to payload
        /// </summary>
        /// <param name="payload"></param>
        public static implicit operator EasyPayload<T>(T payload)
        {
            return new EasyPayload<T> { Payload = payload };
        }

        /// <summary>
        /// Fast convert from payload
        /// </summary>
        /// <param name="payload"></param>
        public static implicit operator T(EasyPayload<T> payload)
        {
            if (payload == null)
                return default;
            else
                return payload.Payload;
        }
    }
}
